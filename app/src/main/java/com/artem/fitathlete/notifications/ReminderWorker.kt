package com.artem.fitathlete.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artem.fitathlete.data.db.AppDatabase
import com.artem.fitathlete.repository.FitnessRepository
import java.time.LocalDate
import java.time.ZoneId

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repository by lazy {
        val db = AppDatabase.get(appContext)
        FitnessRepository(db.workoutDao(), db.mealDao())
    }

    override suspend fun doWork(): Result {
        createChannel()
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val dayStart = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

        val lastWorkout = repository.lastWorkoutMillis()
        if (lastWorkout == null || (System.currentTimeMillis() - lastWorkout) > 3L * 24L * 60L * 60L * 1000L) {
            notify(101, "Пора вернуться в зал", "Артём, прошло больше 3 дней без тренировки. Пора снова включиться.")
        }

        val kcal = repository.dailyCalories(dayStart, dayEnd)
        if (kcal in 1.0..1499.9) {
            notify(102, "Питание просело", "Артём, сегодня меньше 1500 ккал. Проверь, не недоедаешь ли.")
        }
        return Result.success()
    }

    private fun notify(id: Int, title: String, text: String) {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        NotificationManagerCompat.from(applicationContext).notify(id, builder.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Фитнес-напоминания", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    companion object {
        private const val CHANNEL_ID = "fitness_channel"
    }
}

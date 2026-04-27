package com.artem.fitathlete

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.artem.fitathlete.notifications.ReminderWorker
import java.util.concurrent.TimeUnit

class FitAthleteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "fitness_reminders",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}

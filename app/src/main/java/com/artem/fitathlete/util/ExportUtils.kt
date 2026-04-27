
package com.artem.fitathlete.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.artem.fitathlete.data.model.MealEntity
import com.artem.fitathlete.data.model.WorkoutEntity
import java.io.File

object ExportUtils {

    fun shareWorkouts(context: Context, workouts: List<WorkoutEntity>) {
        val file = createDocFile(context, "workouts_export.doc", buildWorkoutsDoc(workouts))
        shareFile(context, file, "Экспорт тренировок", "application/msword")
    }

    fun shareMeals(context: Context, meals: List<MealEntity>) {
        val file = createDocFile(context, "meals_export.doc", buildMealsDoc(meals))
        shareFile(context, file, "Экспорт питания", "application/msword")
    }

    private fun buildWorkoutsDoc(workouts: List<WorkoutEntity>): String = buildString {
        appendLine("<html>")
        appendLine("<head>")
        appendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
        appendLine("<meta charset=\"utf-8\">")
        appendLine("</head>")
        appendLine("<body style=\"font-family:Arial,sans-serif;padding:18px;\">")
        appendLine("<h1>История тренировок</h1>")
        workouts.forEachIndexed { index, item ->
            appendLine("<h2>${index + 1}. ${html(item.exercise)}</h2>")
            appendLine("<p><b>Дата:</b> ${html(formatDate(item.dateMillis))}</p>")
            appendLine("<p><b>Тип:</b> ${html(item.type.title)}</p>")
            if (item.type.name == "STRENGTH") {
                appendLine("<p><b>Подходы:</b> ${item.sets} | <b>Повторения:</b> ${item.reps} | <b>Вес:</b> ${item.weightKg.pretty()} кг</p>")
            } else {
                val cardio = if (item.cardioDistanceMeters > 0) {
                    "Дистанция: ${item.cardioDistanceMeters.pretty()} м"
                } else {
                    "Время: ${item.cardioMinutes.pretty()} мин"
                }
                appendLine("<p><b>${html(cardio)}</b></p>")
            }
            appendLine("<p><b>Нагрузка:</b> ${item.load.pretty()}</p>")
            if (item.note.isNotBlank()) {
                appendLine("<p><b>Заметка:</b> ${html(item.note)}</p>")
            }
            appendLine("<hr>")
        }
        appendLine("</body>")
        appendLine("</html>")
    }

    private fun buildMealsDoc(meals: List<MealEntity>): String = buildString {
        appendLine("<html>")
        appendLine("<head>")
        appendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
        appendLine("<meta charset=\"utf-8\">")
        appendLine("</head>")
        appendLine("<body style=\"font-family:Arial,sans-serif;padding:18px;\">")
        appendLine("<h1>История питания</h1>")
        meals.forEachIndexed { index, item ->
            appendLine("<h2>${index + 1}. ${html(item.dish)}</h2>")
            appendLine("<p><b>Дата:</b> ${html(formatDateTime(item.dateTimeMillis))}</p>")
            appendLine("<p><b>Порция:</b> ${item.portionGrams.pretty()} г</p>")
            appendLine("<p><b>Калории:</b> ${item.totalCalories.pretty()}</p>")
            appendLine("<p><b>Белки:</b> ${item.totalProtein.pretty()} | <b>Жиры:</b> ${item.totalFat.pretty()} | <b>Углеводы:</b> ${item.totalCarbs.pretty()}</p>")
            if (item.note.isNotBlank()) {
                appendLine("<p><b>Заметка:</b> ${html(item.note)}</p>")
            }
            appendLine("<hr>")
        }
        appendLine("</body>")
        appendLine("</html>")
    }

    private fun html(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("\n", "<br>")
    }

    private fun createDocFile(context: Context, fileName: String, content: String): File {
        val folder = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(folder, fileName)
        file.writeText(content, Charsets.UTF_8)
        return file
    }

    private fun shareFile(context: Context, file: File, title: String, mime: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}

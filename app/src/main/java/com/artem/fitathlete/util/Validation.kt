package com.artem.fitathlete.util

import com.artem.fitathlete.data.model.CardioMetric
import com.artem.fitathlete.data.model.WorkoutType

object Validation {
    fun validateWorkout(
        exercise: String,
        type: WorkoutType,
        sets: Int,
        reps: Int,
        weight: Double,
        cardioMetric: CardioMetric,
        cardioValue: Double
    ): String? {
        if (exercise.isBlank()) return "Артём, добавь название упражнения."
        return when (type) {
            WorkoutType.STRENGTH -> when {
                sets <= 0 -> "Артём, проверь: подходы не могут быть нулевыми."
                reps <= 0 -> "Артём, проверь: повторения должны быть больше нуля."
                weight <= 0 -> "Артём, укажи рабочий вес."
                weight > 600 -> "Артём, проверь вес — это уже похоже на фантастику."
                else -> null
            }
            WorkoutType.CARDIO -> when {
                cardioValue <= 0 -> "Артём, кардио без дистанции или времени не считается."
                cardioMetric == CardioMetric.DISTANCE && cardioValue > 100_000 -> "Артём, 100 км за раз — проверь дистанцию."
                cardioMetric == CardioMetric.DURATION && cardioValue > 600 -> "Артём, 10 часов кардио подряд? Проверь минуты."
                else -> null
            }
        }
    }

    fun validateMeal(
        dish: String,
        grams: Double,
        kcal100: Double,
        protein100: Double,
        fat100: Double,
        carbs100: Double
    ): String? {
        if (dish.isBlank()) return "Артём, добавь название блюда."
        if (grams <= 0) return "Артём, порция должна быть больше нуля."
        if (grams > 5000) return "Артём, 5 кг за раз — звучит подозрительно."
        if (kcal100 !in 1.0..900.0) return "Артём, калорийность на 100 г выглядит странно."
        if (protein100 !in 0.0..100.0 || fat100 !in 0.0..100.0 || carbs100 !in 0.0..100.0) {
            return "Артём, БЖУ на 100 г не могут быть вне диапазона 0..100."
        }
        return null
    }
}

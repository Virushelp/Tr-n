package com.artem.fitathlete.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTimeMillis: Long,
    val dish: String,
    val portionGrams: Double,
    val kcalPer100: Double,
    val proteinPer100: Double,
    val fatPer100: Double,
    val carbsPer100: Double,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalFat: Double,
    val totalCarbs: Double,
    val isFavorite: Boolean = false,
    val note: String = ""
)

package com.artem.fitathlete.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val exercise: String,
    val type: WorkoutType,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val cardioDistanceMeters: Double,
    val cardioMinutes: Double,
    val load: Double,
    val isFavorite: Boolean = false,
    val note: String = ""
)

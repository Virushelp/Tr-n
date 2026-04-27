package com.artem.fitathlete.data.db

import androidx.room.TypeConverter
import com.artem.fitathlete.data.model.WorkoutType

class Converters {
    @TypeConverter
    fun fromWorkoutType(value: WorkoutType): String = value.name

    @TypeConverter
    fun toWorkoutType(value: String): WorkoutType = WorkoutType.valueOf(value)
}

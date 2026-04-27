package com.artem.fitathlete.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.artem.fitathlete.data.dao.MealDao
import com.artem.fitathlete.data.dao.WorkoutDao
import com.artem.fitathlete.data.model.MealEntity
import com.artem.fitathlete.data.model.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class, MealEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fit_athlete_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

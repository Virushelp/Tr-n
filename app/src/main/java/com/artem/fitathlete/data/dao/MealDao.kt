package com.artem.fitathlete.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artem.fitathlete.data.model.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY dateTimeMillis DESC, id DESC")
    fun observeAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE isFavorite = 1 ORDER BY dateTimeMillis DESC")
    fun observeFavorites(): Flow<List<MealEntity>>

    @Query("SELECT COALESCE(SUM(totalCalories), 0) FROM meals WHERE dateTimeMillis >= :start AND dateTimeMillis <= :end")
    suspend fun dailyCalories(start: Long, end: Long): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity)

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("UPDATE meals SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)
}

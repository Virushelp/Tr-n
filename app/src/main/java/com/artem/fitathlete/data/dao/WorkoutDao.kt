package com.artem.fitathlete.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artem.fitathlete.data.model.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY dateMillis DESC, id DESC")
    fun observeAll(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE isFavorite = 1 ORDER BY dateMillis DESC")
    fun observeFavorites(): Flow<List<WorkoutEntity>>

    @Query("SELECT COUNT(*) FROM workouts WHERE dateMillis >= :dayStart AND dateMillis <= :dayEnd")
    suspend fun countByDay(dayStart: Long, dayEnd: Long): Int

    @Query("SELECT * FROM workouts WHERE dateMillis >= :dayStart AND dateMillis <= :dayEnd ORDER BY dateMillis ASC, id ASC")
    suspend fun getByDay(dayStart: Long, dayEnd: Long): List<WorkoutEntity>

    @Query("SELECT MAX(dateMillis) FROM workouts")
    suspend fun lastWorkoutMillis(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity)

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)

    @Query("UPDATE workouts SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)
}

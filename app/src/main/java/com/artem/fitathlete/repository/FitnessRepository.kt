package com.artem.fitathlete.repository

import com.artem.fitathlete.data.dao.MealDao
import com.artem.fitathlete.data.dao.WorkoutDao
import com.artem.fitathlete.data.model.MealEntity
import com.artem.fitathlete.data.model.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class FitnessRepository(
    private val workoutDao: WorkoutDao,
    private val mealDao: MealDao
) {
    fun observeWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.observeAll()
    fun observeFavoriteWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.observeFavorites()
    fun observeMeals(): Flow<List<MealEntity>> = mealDao.observeAll()
    fun observeFavoriteMeals(): Flow<List<MealEntity>> = mealDao.observeFavorites()

    suspend fun addWorkout(workout: WorkoutEntity) = workoutDao.insert(workout)
    suspend fun updateWorkout(workout: WorkoutEntity) = workoutDao.update(workout)
    suspend fun deleteWorkout(workout: WorkoutEntity) = workoutDao.delete(workout)

    suspend fun addMeal(meal: MealEntity) = mealDao.insert(meal)
    suspend fun updateMeal(meal: MealEntity) = mealDao.update(meal)
    suspend fun deleteMeal(meal: MealEntity) = mealDao.delete(meal)

    suspend fun toggleWorkoutFavorite(id: Long) = workoutDao.toggleFavorite(id)
    suspend fun toggleMealFavorite(id: Long) = mealDao.toggleFavorite(id)
    suspend fun countWorkoutsByDay(start: Long, end: Long): Int = workoutDao.countByDay(start, end)
    suspend fun workoutsByDay(start: Long, end: Long): List<WorkoutEntity> = workoutDao.getByDay(start, end)
    suspend fun lastWorkoutMillis(): Long? = workoutDao.lastWorkoutMillis()
    suspend fun dailyCalories(start: Long, end: Long): Double = mealDao.dailyCalories(start, end)
}

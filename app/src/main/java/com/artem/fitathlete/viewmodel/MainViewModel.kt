package com.artem.fitathlete.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.fitathlete.data.model.CardioMetric
import com.artem.fitathlete.data.model.MealEntity
import com.artem.fitathlete.data.model.WorkoutEntity
import com.artem.fitathlete.data.model.WorkoutType
import com.artem.fitathlete.repository.FitnessRepository
import com.artem.fitathlete.util.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class HomeMetrics(
    val monthWorkoutCount: Int = 0,
    val totalStrengthLoad: Double = 0.0,
    val weekCalories: Double = 0.0,
    val favoriteExercises: Int = 0,
    val favoriteMeals: Int = 0
)

data class SplitPrompt(
    val dayStart: Long,
    val dayEnd: Long,
    val count: Int
)

class MainViewModel(
    private val repository: FitnessRepository
) : ViewModel() {

    private val workoutQuery = MutableStateFlow("")
    private val workoutTypeFilter = MutableStateFlow<WorkoutType?>(null)
    private val workoutDateFilter = MutableStateFlow<Long?>(null)
    private val mealQuery = MutableStateFlow("")
    private val mealDateFilter = MutableStateFlow<Long?>(null)

    var lastMessage by mutableStateOf<String?>(null)
        private set

    var splitPrompt by mutableStateOf<SplitPrompt?>(null)
        private set

    val workouts = combine(repository.observeWorkouts(), workoutQuery, workoutTypeFilter, workoutDateFilter) { items, query, type, date ->
        items.filter { workout ->
            val queryMatch = query.isBlank() || workout.exercise.contains(query, ignoreCase = true)
            val typeMatch = type == null || workout.type == type
            val dateMatch = date == null || sameDay(workout.dateMillis, date)
            queryMatch && typeMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteWorkouts = repository.observeFavoriteWorkouts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val meals = combine(repository.observeMeals(), mealQuery, mealDateFilter) { items, query, date ->
        items.filter { meal ->
            val queryMatch = query.isBlank() || meal.dish.contains(query, ignoreCase = true)
            val dateMatch = date == null || sameDay(meal.dateTimeMillis, date)
            queryMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoriteMeals = repository.observeFavoriteMeals().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val homeMetrics = combine(repository.observeWorkouts(), repository.observeMeals(), favoriteWorkouts, favoriteMeals) { workoutList, mealList, favWorkouts, favMeals ->
        val now = YearMonth.now()
        val zone = ZoneId.systemDefault()
        val monthStart = now.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val monthEnd = now.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        val weekStart = LocalDate.now(zone).minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()
        val weekEnd = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

        HomeMetrics(
            monthWorkoutCount = workoutList.count { it.dateMillis in monthStart..monthEnd },
            totalStrengthLoad = workoutList.filter { it.type == WorkoutType.STRENGTH }.sumOf { it.load },
            weekCalories = mealList.filter { it.dateTimeMillis in weekStart..weekEnd }.sumOf { it.totalCalories },
            favoriteExercises = favWorkouts.size,
            favoriteMeals = favMeals.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeMetrics())

    fun setWorkoutQuery(value: String) { workoutQuery.value = value }
    fun setWorkoutTypeFilter(value: WorkoutType?) { workoutTypeFilter.value = value }
    fun setWorkoutDateFilter(value: Long?) { workoutDateFilter.value = value }
    fun setMealQuery(value: String) { mealQuery.value = value }
    fun setMealDateFilter(value: Long?) { mealDateFilter.value = value }
    fun clearMessage() { lastMessage = null }

    fun dismissSplitPrompt() {
        splitPrompt = null
        lastMessage = "Тренировка оставлена как есть."
    }

    fun confirmSplitPrompt() {
        val prompt = splitPrompt ?: return
        viewModelScope.launch {
            val items = repository.workoutsByDay(prompt.dayStart, prompt.dayEnd)
            if (items.isEmpty()) {
                splitPrompt = null
                return@launch
            }
            val midpoint = (items.size + 1) / 2
            items.forEachIndexed { index, workout ->
                val cleanNote = workout.note.removePrefix("[Часть 1] ").removePrefix("[Часть 2] ")
                val prefix = if (index < midpoint) "[Часть 1] " else "[Часть 2] "
                repository.updateWorkout(workout.copy(note = prefix + cleanNote))
            }
            splitPrompt = null
            lastMessage = "Тренировка разделена на часть 1 и часть 2."
        }
    }

    fun addWorkout(dateMillis: Long, exercise: String, type: WorkoutType, sets: Int, reps: Int, weight: Double, cardioMetric: CardioMetric, cardioValue: Double, note: String) {
        val error = Validation.validateWorkout(exercise, type, sets, reps, weight, cardioMetric, cardioValue)
        if (error != null) {
            lastMessage = error
            return
        }
        viewModelScope.launch {
            repository.addWorkout(
                WorkoutEntity(
                    dateMillis = dateMillis,
                    exercise = exercise.trim(),
                    type = type,
                    sets = if (type == WorkoutType.STRENGTH) sets else 0,
                    reps = if (type == WorkoutType.STRENGTH) reps else 0,
                    weightKg = if (type == WorkoutType.STRENGTH) weight else 0.0,
                    cardioDistanceMeters = if (type == WorkoutType.CARDIO && cardioMetric == CardioMetric.DISTANCE) cardioValue else 0.0,
                    cardioMinutes = if (type == WorkoutType.CARDIO && cardioMetric == CardioMetric.DURATION) cardioValue else 0.0,
                    load = calculateLoad(type, sets, reps, weight, cardioMetric, cardioValue),
                    note = note.trim()
                )
            )
            val dayStart = startOfDay(dateMillis)
            val dayEnd = endOfDay(dateMillis)
            val count = repository.countWorkoutsByDay(dayStart, dayEnd)
            if (count > 10) splitPrompt = SplitPrompt(dayStart, dayEnd, count)
            else lastMessage = "Тренировка сохранена. Так держать."
        }
    }

    fun updateWorkout(workout: WorkoutEntity, exercise: String, type: WorkoutType, sets: Int, reps: Int, weight: Double, cardioMetric: CardioMetric, cardioValue: Double, note: String) {
        val error = Validation.validateWorkout(exercise, type, sets, reps, weight, cardioMetric, cardioValue)
        if (error != null) {
            lastMessage = error
            return
        }
        viewModelScope.launch {
            repository.updateWorkout(
                workout.copy(
                    exercise = exercise.trim(),
                    type = type,
                    sets = if (type == WorkoutType.STRENGTH) sets else 0,
                    reps = if (type == WorkoutType.STRENGTH) reps else 0,
                    weightKg = if (type == WorkoutType.STRENGTH) weight else 0.0,
                    cardioDistanceMeters = if (type == WorkoutType.CARDIO && cardioMetric == CardioMetric.DISTANCE) cardioValue else 0.0,
                    cardioMinutes = if (type == WorkoutType.CARDIO && cardioMetric == CardioMetric.DURATION) cardioValue else 0.0,
                    load = calculateLoad(type, sets, reps, weight, cardioMetric, cardioValue),
                    note = note.trim()
                )
            )
            lastMessage = "Тренировка обновлена."
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) = viewModelScope.launch {
        repository.deleteWorkout(workout)
        lastMessage = "Тренировка удалена."
    }

    fun addMeal(dateTimeMillis: Long, dish: String, grams: Double, kcal100: Double, protein100: Double, fat100: Double, carbs100: Double, note: String) {
        val error = Validation.validateMeal(dish, grams, kcal100, protein100, fat100, carbs100)
        if (error != null) {
            lastMessage = error
            return
        }
        val factor = grams / 100.0
        viewModelScope.launch {
            repository.addMeal(
                MealEntity(
                    dateTimeMillis = dateTimeMillis,
                    dish = dish.trim(),
                    portionGrams = grams,
                    kcalPer100 = kcal100,
                    proteinPer100 = protein100,
                    fatPer100 = fat100,
                    carbsPer100 = carbs100,
                    totalCalories = kcal100 * factor,
                    totalProtein = protein100 * factor,
                    totalFat = fat100 * factor,
                    totalCarbs = carbs100 * factor,
                    note = note.trim()
                )
            )
            lastMessage = "Приём пищи сохранён. Питание под контролем."
        }
    }

    fun updateMeal(meal: MealEntity, dish: String, grams: Double, kcal100: Double, protein100: Double, fat100: Double, carbs100: Double, note: String) {
        val error = Validation.validateMeal(dish, grams, kcal100, protein100, fat100, carbs100)
        if (error != null) {
            lastMessage = error
            return
        }
        val factor = grams / 100.0
        viewModelScope.launch {
            repository.updateMeal(
                meal.copy(
                    dish = dish.trim(),
                    portionGrams = grams,
                    kcalPer100 = kcal100,
                    proteinPer100 = protein100,
                    fatPer100 = fat100,
                    carbsPer100 = carbs100,
                    totalCalories = kcal100 * factor,
                    totalProtein = protein100 * factor,
                    totalFat = fat100 * factor,
                    totalCarbs = carbs100 * factor,
                    note = note.trim()
                )
            )
            lastMessage = "Приём пищи обновлён."
        }
    }

    fun deleteMeal(meal: MealEntity) = viewModelScope.launch {
        repository.deleteMeal(meal)
        lastMessage = "Приём пищи удалён."
    }

    fun toggleWorkoutFavorite(id: Long) = viewModelScope.launch { repository.toggleWorkoutFavorite(id) }
    fun toggleMealFavorite(id: Long) = viewModelScope.launch { repository.toggleMealFavorite(id) }

    private fun calculateLoad(type: WorkoutType, sets: Int, reps: Int, weight: Double, cardioMetric: CardioMetric, cardioValue: Double): Double =
        when (type) {
            WorkoutType.STRENGTH -> sets * reps * weight
            WorkoutType.CARDIO -> if (cardioMetric == CardioMetric.DISTANCE) cardioValue * 0.1 else cardioValue * 5.0
        }

    private fun sameDay(first: Long, second: Long): Boolean = startOfDay(first) == startOfDay(second)

    private fun startOfDay(millis: Long): Long {
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
    }

    private fun endOfDay(millis: Long): Long {
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    }
}

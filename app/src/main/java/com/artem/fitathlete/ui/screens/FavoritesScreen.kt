package com.artem.fitathlete.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.data.model.WorkoutType
import com.artem.fitathlete.ui.components.GlowingCard
import com.artem.fitathlete.ui.components.SectionTitle
import com.artem.fitathlete.util.formatDate
import com.artem.fitathlete.util.formatDateTime
import com.artem.fitathlete.util.pretty
import com.artem.fitathlete.viewmodel.MainViewModel

@Composable
fun FavoritesScreen(viewModel: MainViewModel) {
    val favoriteWorkouts by viewModel.favoriteWorkouts.collectAsState()
    val favoriteMeals by viewModel.favoriteMeals.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    SectionTitle("Избранное", "Быстрый доступ к любимым упражнениям и блюдам.")
                }
            }
        }

        item { Text("Упражнения", style = MaterialTheme.typography.titleLarge) }

        items(favoriteWorkouts, key = { "w_${it.id}" }) { workout ->
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(workout.exercise, style = MaterialTheme.typography.titleLarge)
                    Text("${workout.type.title} • ${formatDate(workout.dateMillis)}", style = MaterialTheme.typography.bodyMedium)
                    val details = if (workout.type == WorkoutType.STRENGTH) {
                        "Вес: ${workout.weightKg.pretty()} кг • Нагрузка: ${workout.load.pretty()}"
                    } else {
                        val cardioValue = if (workout.cardioDistanceMeters > 0) workout.cardioDistanceMeters.pretty() + " м" else workout.cardioMinutes.pretty() + " мин"
                        "Кардио: $cardioValue • Калории: ${workout.load.pretty()}"
                    }
                    Text(details, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item { Text("Блюда", style = MaterialTheme.typography.titleLarge) }

        items(favoriteMeals, key = { "m_${it.id}" }) { meal ->
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(meal.dish, style = MaterialTheme.typography.titleLarge)
                    Text(formatDateTime(meal.dateTimeMillis), style = MaterialTheme.typography.bodyMedium)
                    Text("Ккал: ${meal.totalCalories.pretty()} • Б: ${meal.totalProtein.pretty()} • Ж: ${meal.totalFat.pretty()} • У: ${meal.totalCarbs.pretty()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

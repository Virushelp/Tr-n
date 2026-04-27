package com.artem.fitathlete.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SportsGymnastics
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.ui.components.GlowingCard
import com.artem.fitathlete.ui.components.MetricChip
import com.artem.fitathlete.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWatermelonTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val metrics by viewModel.homeMetrics.collectAsState()
    val motivationList = listOf(
        "Сегодня ты не просто тренируешься — ты строишь тело, которое будет благодарить тебя завтра.",
        "Каждый подход, каждая минута кардио и каждый нормальный приём пищи — это вклад в твою форму.",
        "Сильная форма собирается не за один день, а за сотни честно прожитых тренировок.",
        "Твой прогресс любит дисциплину: записал, сделал, восстановился, повторил."
    )
    val pagerState = rememberPagerState(pageCount = { motivationList.size })
    val warmupDone = remember { mutableStateOf(false) }
    val foodDone = remember { mutableStateOf(false) }
    val waterDone = remember { mutableStateOf(false) }
    val checkedCount = listOf(warmupDone.value, foodDone.value, waterDone.value).count { it }
    val progress = checkedCount / 3f

    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("FitAthlete Diary", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "Переключить тему",
                        tint = if (isWatermelonTheme) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricChip("Тренировок за месяц", metrics.monthWorkoutCount.toString())
            MetricChip("Силовая нагрузка", "%.0f".format(metrics.totalStrengthLoad))
            MetricChip("Ккал за неделю", "%.0f".format(metrics.weekCalories))
            MetricChip("Избр. упражнений", metrics.favoriteExercises.toString())
            MetricChip("Избр. блюд", metrics.favoriteMeals.toString())
        }

        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Сегодняшний заряд", style = MaterialTheme.typography.titleLarge)
                }
                Text("Отмечай, что уже сделал сегодня.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = warmupDone.value, onClick = { warmupDone.value = !warmupDone.value }, label = { Text("Разминка") })
                    FilterChip(selected = foodDone.value, onClick = { foodDone.value = !foodDone.value }, label = { Text("Нормально поел") })
                    FilterChip(selected = waterDone.value, onClick = { waterDone.value = !waterDone.value }, label = { Text("Вода / режим") })
                }
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp))
                Text("Готовность дня: ${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Мотивация", style = MaterialTheme.typography.titleLarge)
                }
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(motivationList[page], style = MaterialTheme.typography.bodyLarge)
                        Text("${page + 1} / ${motivationList.size}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(motivationList.size) { index ->
                        Spacer(modifier = Modifier.height(8.dp).weight(1f).background(if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))
                    }
                }
            }
        }

        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.SportsGymnastics, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Что есть внутри", style = MaterialTheme.typography.titleLarge)
                }
                Text(
                    "• Тренировки: силовые и кардио\n" +
                        "• Авторасчёт нагрузки\n" +
                        "• Питание, калории и БЖУ\n" +
                        "• Фильтры по дате, типу и названию\n" +
                        "• Аналитика и экспорт Word\n" +
                        "• Избранное и заметки\n" +
                        "• Напоминания про зал и недоедание",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}

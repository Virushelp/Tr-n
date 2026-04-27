package com.artem.fitathlete.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.data.model.WorkoutType
import com.artem.fitathlete.ui.components.DetailedMetricChart
import com.artem.fitathlete.ui.components.GlowingCard
import com.artem.fitathlete.ui.components.InteractivePieChart
import com.artem.fitathlete.ui.components.MetricChartMode
import com.artem.fitathlete.ui.components.MetricChip
import com.artem.fitathlete.ui.components.PieSection
import com.artem.fitathlete.ui.components.SectionTitle
import com.artem.fitathlete.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class PieMode { WORKOUTS, MACROS }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val workouts by viewModel.workouts.collectAsState()
    val meals by viewModel.meals.collectAsState()

    val zone = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("dd.MM", Locale("ru"))
    val chartMode = remember { mutableStateOf(MetricChartMode.LINE) }
    val pieMode = remember { mutableStateOf(PieMode.WORKOUTS) }

    val last7Days = (6 downTo 0).map { offset -> LocalDate.now(zone).minusDays(offset.toLong()) }
    val labels = last7Days.map { it.format(formatter) }

    val loadSeries = last7Days.map { day ->
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        workouts.filter { it.dateMillis in start..end }.sumOf { it.load }.toFloat()
    }
    val caloriesSeries = last7Days.map { day ->
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        meals.filter { it.dateTimeMillis in start..end }.sumOf { it.totalCalories }.toFloat()
    }
    val proteinSeries = last7Days.map { day ->
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        meals.filter { it.dateTimeMillis in start..end }.sumOf { it.totalProtein }.toFloat()
    }
    val cardioMinutesSeries = last7Days.map { day ->
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        workouts.filter { it.dateMillis in start..end && it.type == WorkoutType.CARDIO }.sumOf { it.cardioMinutes }.toFloat()
    }

    val totalStrengthLoad = workouts.filter { it.type == WorkoutType.STRENGTH }.sumOf { it.load }
    val cardioSessions = workouts.count { it.type == WorkoutType.CARDIO }
    val strengthSessions = workouts.count { it.type == WorkoutType.STRENGTH }

    val workoutPie = listOf(
        PieSection("Силовые", strengthSessions.toFloat(), MaterialTheme.colorScheme.primary),
        PieSection("Кардио", cardioSessions.toFloat(), MaterialTheme.colorScheme.secondary)
    )
    val macroPie = listOf(
        PieSection("Белки", meals.sumOf { it.totalProtein }.toFloat(), MaterialTheme.colorScheme.primary),
        PieSection("Жиры", meals.sumOf { it.totalFat }.toFloat(), Color(0xFFFFB347)),
        PieSection("Углеводы", meals.sumOf { it.totalCarbs }.toFloat(), MaterialTheme.colorScheme.secondary)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Аналитика", "Графики можно переключать: линия или столбцы. Ниже есть круговая диаграмма по режимам.")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricChip("Всего тренировок", workouts.size.toString())
                    MetricChip("Силовых", strengthSessions.toString())
                    MetricChip("Кардио", cardioSessions.toString())
                    MetricChip("Сумм. нагрузка", "%.0f".format(totalStrengthLoad))
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = chartMode.value == MetricChartMode.LINE, onClick = { chartMode.value = MetricChartMode.LINE }, label = { Text("Линия") })
                    FilterChip(selected = chartMode.value == MetricChartMode.BAR, onClick = { chartMode.value = MetricChartMode.BAR }, label = { Text("Столбцы") })
                }
            }
        }

        DetailedMetricChart("Нагрузка за 7 дней", "Суммарная нагрузка по тренировкам за каждый день.", loadSeries, labels, "", chartMode.value)
        DetailedMetricChart("Калории за 7 дней", "Помогает понять, не проседает ли питание на фоне тренировок.", caloriesSeries, labels, "ккал", chartMode.value)
        DetailedMetricChart("Белок за 7 дней", "Смотри, насколько стабильно держится белок по дням.", proteinSeries, labels, "г", chartMode.value)
        DetailedMetricChart("Кардио по минутам", "Минуты кардио за день — удобно видеть общую аэробную нагрузку.", cardioMinutesSeries, labels, "мин", chartMode.value)

        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Круговая диаграмма", style = MaterialTheme.typography.titleLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = pieMode.value == PieMode.WORKOUTS, onClick = { pieMode.value = PieMode.WORKOUTS }, label = { Text("Типы тренировок") })
                    FilterChip(selected = pieMode.value == PieMode.MACROS, onClick = { pieMode.value = PieMode.MACROS }, label = { Text("БЖУ") })
                }
            }
        }

        InteractivePieChart(
            title = if (pieMode.value == PieMode.WORKOUTS) "Распределение тренировок" else "Распределение БЖУ",
            subtitle = if (pieMode.value == PieMode.WORKOUTS) "Показывает, чего у тебя больше: силовых или кардио." else "Показывает общий баланс белков, жиров и углеводов.",
            sections = if (pieMode.value == PieMode.WORKOUTS) workoutPie else macroPie
        )

        GlowingCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Как читать графики", style = MaterialTheme.typography.titleLarge)
                Text(
                    "• Переключайся между линией и столбцами\n" +
                        "• Смотри среднее, максимум, минимум и дельту\n" +
                        "• Круговая диаграмма переключается между типами тренировок и БЖУ\n" +
                        "• Так легче увидеть, где растёт прогресс, а где есть просадка",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

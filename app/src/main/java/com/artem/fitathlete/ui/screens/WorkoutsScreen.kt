@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.artem.fitathlete.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.RunCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.data.model.CardioMetric
import com.artem.fitathlete.data.model.WorkoutEntity
import com.artem.fitathlete.data.model.WorkoutType
import com.artem.fitathlete.ui.components.FitnessTextField
import com.artem.fitathlete.ui.components.GlowingCard
import com.artem.fitathlete.ui.components.PrimaryButton
import com.artem.fitathlete.ui.components.SectionTitle
import com.artem.fitathlete.ui.components.ExportSelectionDialog
import com.artem.fitathlete.util.ExportUtils
import com.artem.fitathlete.util.formatDate
import com.artem.fitathlete.util.pretty
import com.artem.fitathlete.viewmodel.MainViewModel
import kotlin.math.roundToInt

@Composable
fun WorkoutsScreen(viewModel: MainViewModel) {
    val workouts by viewModel.workouts.collectAsState()
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    var exercise by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(WorkoutType.STRENGTH) }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var cardioMetric by remember { mutableStateOf(CardioMetric.DISTANCE) }
    var cardioValue by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var search by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<WorkoutType?>(null) }
    var filterDateMillis by remember { mutableStateOf<Long?>(null) }
    var showFilterDatePicker by remember { mutableStateOf(false) }
    var editingWorkout by remember { mutableStateOf<WorkoutEntity?>(null) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    val filterPickerState = rememberDatePickerState(initialSelectedDateMillis = filterDateMillis ?: System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showFilterDatePicker) {
        DatePickerDialog(onDismissRequest = { showFilterDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    filterDateMillis = filterPickerState.selectedDateMillis
                    viewModel.setWorkoutDateFilter(filterDateMillis)
                    showFilterDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showFilterDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(state = filterPickerState) }
    }

    viewModel.splitPrompt?.let { splitPrompt ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissSplitPrompt() },
            title = { Text("Разбить тренировку?") },
            text = { Text("У тебя уже ${splitPrompt.count} упражнений за этот день. Разбить тренировку на часть 1 и часть 2?") },
            confirmButton = { TextButton(onClick = { viewModel.confirmSplitPrompt() }) { Text("Да") } },
            dismissButton = { TextButton(onClick = { viewModel.dismissSplitPrompt() }) { Text("Нет") } }
        )
    }


    if (showExportDialog) {
        ExportSelectionDialog(
            title = "Экспорт тренировок",
            items = workouts,
            getDate = { it.dateMillis },
            getKey = { it.id },
            getLabel = { item -> "${formatDate(item.dateMillis)} • ${item.exercise} • ${item.type.title}" },
            onDismiss = { showExportDialog = false },
            onConfirm = { selected ->
                if (selected.isNotEmpty()) {
                    ExportUtils.shareWorkouts(context, selected)
                }
                showExportDialog = false
            }
        )
    }

    editingWorkout?.let { workout ->
        EditWorkoutDialog(
            workout = workout,
            onDismiss = { editingWorkout = null },
            onSave = { updatedType, updatedExercise, updatedSets, updatedReps, updatedWeight, updatedCardioMetric, updatedCardioValue, updatedNote ->
                viewModel.updateWorkout(
                    workout = workout,
                    exercise = updatedExercise,
                    type = updatedType,
                    sets = updatedSets,
                    reps = updatedReps,
                    weight = updatedWeight,
                    cardioMetric = updatedCardioMetric,
                    cardioValue = updatedCardioValue,
                    note = updatedNote
                )
                editingWorkout = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Тренировки", "Добавляй силовые и кардио. Свайп влево по записи — изменить или удалить.")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(selected = type == WorkoutType.STRENGTH, onClick = { type = WorkoutType.STRENGTH }, label = { Text("Силовая") }, leadingIcon = { Icon(Icons.Rounded.FitnessCenter, null) })
                        FilterChip(selected = type == WorkoutType.CARDIO, onClick = { type = WorkoutType.CARDIO }, label = { Text("Кардио") }, leadingIcon = { Icon(Icons.Rounded.RunCircle, null) })
                        AssistChip(onClick = { showDatePicker = true }, label = { Text("Дата: ${formatDate(dateMillis)}") })
                    }

                    FitnessTextField(value = exercise, onValueChange = { exercise = it }, label = { Text("Упражнение") }, modifier = Modifier.fillMaxWidth())

                    if (type == WorkoutType.STRENGTH) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            FitnessTextField(value = sets, onValueChange = { sets = it.filter(Char::isDigit) }, label = { Text("Подходы") }, modifier = Modifier.weight(1f))
                            FitnessTextField(value = reps, onValueChange = { reps = it.filter(Char::isDigit) }, label = { Text("Повторения") }, modifier = Modifier.weight(1f))
                            FitnessTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Вес, кг") }, modifier = Modifier.weight(1f))
                        }
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(selected = cardioMetric == CardioMetric.DISTANCE, onClick = { cardioMetric = CardioMetric.DISTANCE }, label = { Text("Метры") })
                            FilterChip(selected = cardioMetric == CardioMetric.DURATION, onClick = { cardioMetric = CardioMetric.DURATION }, label = { Text("Минуты") })
                        }
                        FitnessTextField(
                            value = cardioValue,
                            onValueChange = { cardioValue = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text(if (cardioMetric == CardioMetric.DISTANCE) "Дистанция, м" else "Время, мин") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    FitnessTextField(value = note, onValueChange = { note = it }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), singleLine = false)

                    PrimaryButton(text = "Сохранить тренировку") {
                        viewModel.addWorkout(
                            dateMillis = dateMillis,
                            exercise = exercise,
                            type = type,
                            sets = sets.toIntOrNull() ?: 0,
                            reps = reps.toIntOrNull() ?: 0,
                            weight = weight.toDoubleOrNull() ?: 0.0,
                            cardioMetric = cardioMetric,
                            cardioValue = cardioValue.toDoubleOrNull() ?: 0.0,
                            note = note
                        )
                        exercise = ""; sets = ""; reps = ""; weight = ""; cardioValue = ""; note = ""
                    }
                }
            }
        }

        item {
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Фильтры и экспорт")
                    FitnessTextField(value = search, onValueChange = { search = it; viewModel.setWorkoutQuery(it) }, label = { Text("Поиск по упражнению") }, modifier = Modifier.fillMaxWidth())
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(selected = selectedTypeFilter == null, onClick = { selectedTypeFilter = null; viewModel.setWorkoutTypeFilter(null) }, label = { Text("Все") })
                        FilterChip(selected = selectedTypeFilter == WorkoutType.STRENGTH, onClick = { selectedTypeFilter = WorkoutType.STRENGTH; viewModel.setWorkoutTypeFilter(WorkoutType.STRENGTH) }, label = { Text("Силовые") })
                        FilterChip(selected = selectedTypeFilter == WorkoutType.CARDIO, onClick = { selectedTypeFilter = WorkoutType.CARDIO; viewModel.setWorkoutTypeFilter(WorkoutType.CARDIO) }, label = { Text("Кардио") })
                        AssistChip(onClick = { showFilterDatePicker = true }, label = { Text(filterDateMillis?.let { formatDate(it) } ?: "Фильтр по дате") })
                        AssistChip(onClick = { filterDateMillis = null; viewModel.setWorkoutDateFilter(null) }, label = { Text("Сбросить дату") })
                    }
                    Button(onClick = { showExportDialog = true }) { Text("Экспорт DOC") }
                }
            }
        }

        items(workouts, key = { it.id }) { workout ->
            SwipeableWorkoutItem(
                workout = workout,
                onToggleFavorite = { viewModel.toggleWorkoutFavorite(workout.id) },
                onEdit = { editingWorkout = workout },
                onDelete = { viewModel.deleteWorkout(workout) }
            )
        }
    }
}

@Composable
private fun SwipeableWorkoutItem(
    workout: WorkoutEntity,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current
    val actionsWidthPx = with(density) { 148.dp.toPx() }
    var offsetX by remember(workout.id) { mutableFloatStateOf(0f) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterEnd).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large, modifier = Modifier.padding(end = 8.dp)) {
                IconButton(onClick = { offsetX = 0f; onEdit() }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Изменить", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f), shape = MaterialTheme.shapes.large) {
                IconButton(onClick = { offsetX = 0f; onDelete() }) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        GlowingCard(
            modifier = Modifier.fillMaxWidth().offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(workout.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount -> offsetX = (offsetX + dragAmount).coerceIn(-actionsWidthPx, 0f) },
                        onDragEnd = { offsetX = if (offsetX < -actionsWidthPx * 0.35f) -actionsWidthPx else 0f }
                    )
                }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(workout.exercise, style = MaterialTheme.typography.titleLarge)
                        Text("${workout.type.title} • ${formatDate(workout.dateMillis)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(if (workout.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (workout.type == WorkoutType.STRENGTH) {
                    Text("Подходы: ${workout.sets} • Повторения: ${workout.reps} • Вес: ${workout.weightKg.pretty()} кг", style = MaterialTheme.typography.bodyLarge)
                } else {
                    Text(if (workout.cardioDistanceMeters > 0) "Дистанция: ${workout.cardioDistanceMeters.pretty()} м" else "Время: ${workout.cardioMinutes.pretty()} мин", style = MaterialTheme.typography.bodyLarge)
                }
                Text("Нагрузка: ${workout.load.pretty()}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                if (workout.note.isNotBlank()) Text("Заметка: ${workout.note}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EditWorkoutDialog(
    workout: WorkoutEntity,
    onDismiss: () -> Unit,
    onSave: (WorkoutType, String, Int, Int, Double, CardioMetric, Double, String) -> Unit
) {
    var exercise by remember(workout.id) { mutableStateOf(workout.exercise) }
    var type by remember(workout.id) { mutableStateOf(workout.type) }
    var sets by remember(workout.id) { mutableStateOf(if (workout.sets == 0) "" else workout.sets.toString()) }
    var reps by remember(workout.id) { mutableStateOf(if (workout.reps == 0) "" else workout.reps.toString()) }
    var weight by remember(workout.id) { mutableStateOf(if (workout.weightKg == 0.0) "" else workout.weightKg.toString()) }
    val initialMetric = if (workout.cardioDistanceMeters > 0) CardioMetric.DISTANCE else CardioMetric.DURATION
    var cardioMetric by remember(workout.id) { mutableStateOf(initialMetric) }
    var cardioValue by remember(workout.id) { mutableStateOf(if (workout.cardioDistanceMeters > 0) workout.cardioDistanceMeters.toString() else if (workout.cardioMinutes > 0) workout.cardioMinutes.toString() else "") }
    var note by remember(workout.id) { mutableStateOf(workout.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить тренировку") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(selected = type == WorkoutType.STRENGTH, onClick = { type = WorkoutType.STRENGTH }, label = { Text("Силовая") })
                    FilterChip(selected = type == WorkoutType.CARDIO, onClick = { type = WorkoutType.CARDIO }, label = { Text("Кардио") })
                }
                FitnessTextField(value = exercise, onValueChange = { exercise = it }, label = { Text("Упражнение") }, modifier = Modifier.fillMaxWidth())
                if (type == WorkoutType.STRENGTH) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FitnessTextField(value = sets, onValueChange = { sets = it.filter(Char::isDigit) }, label = { Text("Подходы") }, modifier = Modifier.weight(1f))
                        FitnessTextField(value = reps, onValueChange = { reps = it.filter(Char::isDigit) }, label = { Text("Повторения") }, modifier = Modifier.weight(1f))
                        FitnessTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Вес") }, modifier = Modifier.weight(1f))
                    }
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(selected = cardioMetric == CardioMetric.DISTANCE, onClick = { cardioMetric = CardioMetric.DISTANCE }, label = { Text("Метры") })
                        FilterChip(selected = cardioMetric == CardioMetric.DURATION, onClick = { cardioMetric = CardioMetric.DURATION }, label = { Text("Минуты") })
                    }
                    FitnessTextField(value = cardioValue, onValueChange = { cardioValue = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text(if (cardioMetric == CardioMetric.DISTANCE) "Дистанция, м" else "Время, мин") }, modifier = Modifier.fillMaxWidth())
                }
                FitnessTextField(value = note, onValueChange = { note = it }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), singleLine = false)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(type, exercise, sets.toIntOrNull() ?: 0, reps.toIntOrNull() ?: 0, weight.toDoubleOrNull() ?: 0.0, cardioMetric, cardioValue.toDoubleOrNull() ?: 0.0, note) }) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

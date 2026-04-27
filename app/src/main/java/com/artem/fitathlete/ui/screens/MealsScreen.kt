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
import androidx.compose.material.icons.rounded.Restaurant
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
import com.artem.fitathlete.data.model.MealEntity
import com.artem.fitathlete.ui.components.FitnessTextField
import com.artem.fitathlete.ui.components.GlowingCard
import com.artem.fitathlete.ui.components.PrimaryButton
import com.artem.fitathlete.ui.components.SectionTitle
import com.artem.fitathlete.ui.components.ExportSelectionDialog
import com.artem.fitathlete.util.ExportUtils
import com.artem.fitathlete.util.formatDate
import com.artem.fitathlete.util.formatDateTime
import com.artem.fitathlete.util.pretty
import com.artem.fitathlete.viewmodel.MainViewModel
import kotlin.math.roundToInt

@Composable
fun MealsScreen(viewModel: MainViewModel) {
    val meals by viewModel.meals.collectAsState()
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    var dish by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }
    var kcal by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var dateTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var search by remember { mutableStateOf("") }
    var filterDateMillis by remember { mutableStateOf<Long?>(null) }
    var showFilterDatePicker by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<MealEntity?>(null) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateTimeMillis)
    val filterDatePickerState = rememberDatePickerState(initialSelectedDateMillis = filterDateMillis ?: System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateTimeMillis = it }
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
                    filterDateMillis = filterDatePickerState.selectedDateMillis
                    viewModel.setMealDateFilter(filterDateMillis)
                    showFilterDatePicker = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showFilterDatePicker = false }) { Text("Отмена") } }
        ) { DatePicker(state = filterDatePickerState) }
    }


    if (showExportDialog) {
        ExportSelectionDialog(
            title = "Экспорт питания",
            items = meals,
            getDate = { it.dateTimeMillis },
            getKey = { it.id },
            getLabel = { item -> "${formatDateTime(item.dateTimeMillis)} • ${item.dish} • ${item.totalCalories.pretty()} ккал" },
            onDismiss = { showExportDialog = false },
            onConfirm = { selected ->
                if (selected.isNotEmpty()) {
                    ExportUtils.shareMeals(context, selected)
                }
                showExportDialog = false
            }
        )
    }

    editingMeal?.let { meal ->
        EditMealDialog(
            meal = meal,
            onDismiss = { editingMeal = null },
            onSave = { newDish, newGrams, newKcal, newProtein, newFat, newCarbs, newNote ->
                viewModel.updateMeal(
                    meal = meal,
                    dish = newDish,
                    grams = newGrams,
                    kcal100 = newKcal,
                    protein100 = newProtein,
                    fat100 = newFat,
                    carbs100 = newCarbs,
                    note = newNote
                )
                editingMeal = null
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
                    SectionTitle("Питание", "Свайп влево по записи — изменить или удалить.")
                    AssistChip(onClick = { showDatePicker = true }, label = { Text("Дата: ${formatDate(dateTimeMillis)}") }, leadingIcon = { Icon(Icons.Rounded.Restaurant, null) })
                    FitnessTextField(value = dish, onValueChange = { dish = it }, label = { Text("Блюдо") }, modifier = Modifier.fillMaxWidth())

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        FitnessTextField(value = grams, onValueChange = { grams = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Порция, г") }, modifier = Modifier.weight(1f))
                        FitnessTextField(value = kcal, onValueChange = { kcal = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Ккал / 100 г") }, modifier = Modifier.weight(1f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        FitnessTextField(value = proteins, onValueChange = { proteins = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Белки / 100") }, modifier = Modifier.weight(1f))
                        FitnessTextField(value = fats, onValueChange = { fats = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Жиры / 100") }, modifier = Modifier.weight(1f))
                        FitnessTextField(value = carbs, onValueChange = { carbs = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Углеводы / 100") }, modifier = Modifier.weight(1f))
                    }

                    FitnessTextField(value = note, onValueChange = { note = it }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), singleLine = false)

                    val factor = (grams.toDoubleOrNull() ?: 0.0) / 100.0
                    val caloriesPreview = (kcal.toDoubleOrNull() ?: 0.0) * factor
                    val proteinPreview = (proteins.toDoubleOrNull() ?: 0.0) * factor
                    Text("Для порции выйдет примерно: ${caloriesPreview.pretty()} ккал и ${proteinPreview.pretty()} г белка", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)

                    PrimaryButton(text = "Сохранить приём пищи") {
                        viewModel.addMeal(
                            dateTimeMillis = dateTimeMillis,
                            dish = dish,
                            grams = grams.toDoubleOrNull() ?: 0.0,
                            kcal100 = kcal.toDoubleOrNull() ?: 0.0,
                            protein100 = proteins.toDoubleOrNull() ?: 0.0,
                            fat100 = fats.toDoubleOrNull() ?: 0.0,
                            carbs100 = carbs.toDoubleOrNull() ?: 0.0,
                            note = note
                        )
                        dish = ""; grams = ""; kcal = ""; proteins = ""; fats = ""; carbs = ""; note = ""
                    }
                }
            }
        }

        item {
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("Поиск и экспорт")
                    FitnessTextField(value = search, onValueChange = { search = it; viewModel.setMealQuery(it) }, label = { Text("Поиск по блюдам") }, modifier = Modifier.fillMaxWidth())
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AssistChip(onClick = { showFilterDatePicker = true }, label = { Text(filterDateMillis?.let { formatDate(it) } ?: "Фильтр по дате") })
                        FilterChip(selected = filterDateMillis == null, onClick = { filterDateMillis = null; viewModel.setMealDateFilter(null) }, label = { Text("Все даты") })
                    }
                    Button(onClick = { showExportDialog = true }) { Text("Экспорт DOC") }
                }
            }
        }

        items(meals, key = { it.id }) { meal ->
            SwipeableMealItem(
                meal = meal,
                onToggleFavorite = { viewModel.toggleMealFavorite(meal.id) },
                onEdit = { editingMeal = meal },
                onDelete = { viewModel.deleteMeal(meal) }
            )
        }
    }
}

@Composable
private fun SwipeableMealItem(
    meal: MealEntity,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val density = LocalDensity.current
    val actionsWidthPx = with(density) { 148.dp.toPx() }
    var offsetX by remember(meal.id) { mutableFloatStateOf(0f) }

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
            modifier = Modifier.fillMaxWidth().offset { IntOffset(offsetX.roundToInt(), 0) }.pointerInput(meal.id) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount -> offsetX = (offsetX + dragAmount).coerceIn(-actionsWidthPx, 0f) },
                    onDragEnd = { offsetX = if (offsetX < -actionsWidthPx * 0.35f) -actionsWidthPx else 0f }
                )
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(meal.dish, style = MaterialTheme.typography.titleLarge)
                        Text(formatDateTime(meal.dateTimeMillis), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(if (meal.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Text("Порция: ${meal.portionGrams.pretty()} г • Калории: ${meal.totalCalories.pretty()}", style = MaterialTheme.typography.bodyLarge)
                Text("Б: ${meal.totalProtein.pretty()} • Ж: ${meal.totalFat.pretty()} • У: ${meal.totalCarbs.pretty()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                if (meal.note.isNotBlank()) Text("Заметка: ${meal.note}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EditMealDialog(
    meal: MealEntity,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Double, Double, Double, String) -> Unit
) {
    var dish by remember(meal.id) { mutableStateOf(meal.dish) }
    var grams by remember(meal.id) { mutableStateOf(meal.portionGrams.toString()) }
    var kcal by remember(meal.id) { mutableStateOf(meal.kcalPer100.toString()) }
    var proteins by remember(meal.id) { mutableStateOf(meal.proteinPer100.toString()) }
    var fats by remember(meal.id) { mutableStateOf(meal.fatPer100.toString()) }
    var carbs by remember(meal.id) { mutableStateOf(meal.carbsPer100.toString()) }
    var note by remember(meal.id) { mutableStateOf(meal.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить питание") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FitnessTextField(value = dish, onValueChange = { dish = it }, label = { Text("Блюдо") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FitnessTextField(value = grams, onValueChange = { grams = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Порция") }, modifier = Modifier.weight(1f))
                    FitnessTextField(value = kcal, onValueChange = { kcal = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Ккал") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FitnessTextField(value = proteins, onValueChange = { proteins = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Белки") }, modifier = Modifier.weight(1f))
                    FitnessTextField(value = fats, onValueChange = { fats = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Жиры") }, modifier = Modifier.weight(1f))
                    FitnessTextField(value = carbs, onValueChange = { carbs = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Углеводы") }, modifier = Modifier.weight(1f))
                }
                FitnessTextField(value = note, onValueChange = { note = it }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), singleLine = false)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    dish,
                    grams.toDoubleOrNull() ?: 0.0,
                    kcal.toDoubleOrNull() ?: 0.0,
                    proteins.toDoubleOrNull() ?: 0.0,
                    fats.toDoubleOrNull() ?: 0.0,
                    carbs.toDoubleOrNull() ?: 0.0,
                    note
                )
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

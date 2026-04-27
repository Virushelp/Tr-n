
package com.artem.fitathlete.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.util.formatDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> ExportSelectionDialog(
    title: String,
    items: List<T>,
    getDate: (T) -> Long,
    getKey: (T) -> Long,
    getLabel: (T) -> String,
    onDismiss: () -> Unit,
    onConfirm: (List<T>) -> Unit
) {
    val startDate = remember { mutableStateOf<Long?>(null) }
    val endDate = remember { mutableStateOf<Long?>(null) }
    val showStartPicker = remember { mutableStateOf(false) }
    val showEndPicker = remember { mutableStateOf(false) }
    val selectedKeys = remember { mutableStateListOf<Long>() }

    val startPicker = rememberDatePickerState(initialSelectedDateMillis = startDate.value ?: System.currentTimeMillis())
    val endPicker = rememberDatePickerState(initialSelectedDateMillis = endDate.value ?: System.currentTimeMillis())

    if (showStartPicker.value) {
        DatePickerDialog(
            onDismissRequest = { showStartPicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate.value = startPicker.selectedDateMillis
                    showStartPicker.value = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showStartPicker.value = false }) { Text("Отмена") } }
        ) { DatePicker(state = startPicker) }
    }

    if (showEndPicker.value) {
        DatePickerDialog(
            onDismissRequest = { showEndPicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate.value = endPicker.selectedDateMillis
                    showEndPicker.value = false
                }) { Text("ОК") }
            },
            dismissButton = { TextButton(onClick = { showEndPicker.value = false }) { Text("Отмена") } }
        ) { DatePicker(state = endPicker) }
    }

    val filtered = items.filter { item ->
        val date = getDate(item)
        val startOk = startDate.value == null || date >= startDate.value!!
        val endOk = endDate.value == null || date <= endDate.value!! + 86_399_999L
        startOk && endOk
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Выбери период и нужные записи. Если ничего не отмечать, экспортируются все записи за выбранный период.",
                    style = MaterialTheme.typography.bodyMedium
                )

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { showStartPicker.value = true }, label = { Text(startDate.value?.let { "С: ${formatDate(it)}" } ?: "Дата начала") })
                    AssistChip(onClick = { showEndPicker.value = true }, label = { Text(endDate.value?.let { "По: ${formatDate(it)}" } ?: "Дата конца") })
                    FilterChip(selected = false, onClick = { startDate.value = null; endDate.value = null }, label = { Text("Сбросить период") })
                    FilterChip(selected = false, onClick = { selectedKeys.clear(); filtered.forEach { selectedKeys.add(getKey(it)) } }, label = { Text("Выбрать всё") })
                    FilterChip(selected = false, onClick = { selectedKeys.clear() }, label = { Text("Снять выбор") })
                }

                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filtered.isEmpty()) {
                        Text("За выбранный период записей нет.", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        filtered.forEach { item ->
                            val key = getKey(item)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Checkbox(checked = selectedKeys.contains(key), onCheckedChange = { checked ->
                                    if (checked) selectedKeys.add(key) else selectedKeys.remove(key)
                                })
                                Text(getLabel(item), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val result = if (selectedKeys.isEmpty()) filtered else filtered.filter { selectedKeys.contains(getKey(it)) }
                onConfirm(result)
            }) { Text("Экспорт") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

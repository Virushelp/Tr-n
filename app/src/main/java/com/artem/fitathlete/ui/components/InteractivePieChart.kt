package com.artem.fitathlete.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.util.pretty

data class PieSection(
    val label: String,
    val value: Float,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractivePieChart(
    title: String,
    subtitle: String,
    sections: List<PieSection>,
    modifier: Modifier = Modifier
) {
    val filtered = sections.filter { it.value > 0f }
    val total = filtered.sumOf { it.value.toDouble() }.toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        }

        if (total <= 0f) {
            Text("Недостаточно данных для круговой диаграммы.", style = MaterialTheme.typography.bodyLarge)
            return@Column
        }

        Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            val diameter = minOf(size.width, size.height) * 0.8f
            val topLeftX = (size.width - diameter) / 2f
            val topLeftY = (size.height - diameter) / 2f
            var startAngle = -90f

            filtered.forEach { item ->
                val sweep = (item.value / total) * 360f
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(topLeftX, topLeftY),
                    size = Size(diameter, diameter),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 54f)
                )
                startAngle += sweep
            }
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filtered.forEach { item ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.padding(top = 3.dp).size(14.dp).background(item.color, CircleShape))
                    Text("${item.label}: ${item.value.toDouble().pretty()}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

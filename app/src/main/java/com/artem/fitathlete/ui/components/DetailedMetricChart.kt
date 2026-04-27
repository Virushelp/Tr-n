
package com.artem.fitathlete.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.artem.fitathlete.util.pretty
import kotlin.math.max

enum class MetricChartMode { LINE, BAR }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailedMetricChart(
    title: String,
    subtitle: String,
    points: List<Float>,
    labels: List<String>,
    unit: String,
    mode: MetricChartMode,
    modifier: Modifier = Modifier
) {
    val safePoints = if (points.isEmpty()) listOf(0f) else points
    val maxValue = max(safePoints.maxOrNull() ?: 1f, 1f)
    val minValue = safePoints.minOrNull() ?: 0f
    val avgValue = safePoints.average().toFloat()
    val lastValue = safePoints.lastOrNull() ?: 0f
    val firstValue = safePoints.firstOrNull() ?: 0f
    val delta = lastValue - firstValue

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surface, RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = onSurface.copy(alpha = 0.65f))
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricChip("Среднее", "${avgValue.toDouble().pretty()} $unit")
            MetricChip("Максимум", "${maxValue.toDouble().pretty()} $unit")
            MetricChip("Минимум", "${minValue.toDouble().pretty()} $unit")
            MetricChip("Дельта", (if (delta >= 0) "+" else "") + "${delta.toDouble().pretty()} $unit")
        }

        Canvas(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            val leftPadding = 66f
            val rightPadding = 20f
            val topPadding = 20f
            val bottomPadding = 32f
            val chartWidth = size.width - leftPadding - rightPadding
            val chartHeight = size.height - topPadding - bottomPadding
            val stepX = if (safePoints.size > 1) chartWidth / (safePoints.size - 1) else chartWidth

            val axisPaint = Paint().apply {
                color = android.graphics.Color.argb(170, 120, 120, 120)
                textSize = 24f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            val pointPaint = Paint().apply {
                color = android.graphics.Color.argb(185, 120, 120, 120)
                textSize = 22f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            for (i in 0..4) {
                val fraction = i / 4f
                val y = topPadding + chartHeight * fraction
                drawLine(
                    color = onSurface.copy(alpha = 0.10f),
                    start = Offset(leftPadding, y),
                    end = Offset(size.width - rightPadding, y),
                    strokeWidth = 2f
                )
                val valueAtLevel = maxValue * (1f - fraction)
                drawContext.canvas.nativeCanvas.drawText(
                    "${valueAtLevel.toDouble().pretty()}${if (unit.isNotBlank()) " $unit" else ""}",
                    leftPadding - 8f,
                    y + 8f,
                    axisPaint
                )
            }

            val avgY = topPadding + chartHeight - (avgValue / maxValue) * chartHeight
            drawLine(
                color = secondary.copy(alpha = 0.30f),
                start = Offset(leftPadding, avgY),
                end = Offset(size.width - rightPadding, avgY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )
            drawContext.canvas.nativeCanvas.drawText(
                "ср. ${avgValue.toDouble().pretty()}",
                size.width - rightPadding,
                avgY - 8f,
                axisPaint.apply { textAlign = Paint.Align.RIGHT }
            )

            if (mode == MetricChartMode.LINE) {
                val linePath = Path()
                val fillPath = Path()

                safePoints.forEachIndexed { index, value ->
                    val x = leftPadding + stepX * index
                    val y = topPadding + chartHeight - (value / maxValue) * chartHeight
                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, topPadding + chartHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                }

                fillPath.lineTo(leftPadding + stepX * (safePoints.size - 1), topPadding + chartHeight)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primary.copy(alpha = 0.30f), primary.copy(alpha = 0.02f)),
                        startY = topPadding,
                        endY = topPadding + chartHeight
                    )
                )
                drawPath(path = linePath, color = primary, style = Stroke(width = 7f))

                safePoints.forEachIndexed { index, value ->
                    val x = leftPadding + stepX * index
                    val y = topPadding + chartHeight - (value / maxValue) * chartHeight
                    drawCircle(color = primary, radius = 7f, center = Offset(x, y))
                    drawCircle(color = surface, radius = 3f, center = Offset(x, y))
                    drawContext.canvas.nativeCanvas.drawText(value.toDouble().pretty(), x, y - 16f, pointPaint)
                }
            } else {
                val barWidth = max(24f, chartWidth / (safePoints.size * 1.8f))
                safePoints.forEachIndexed { index, value ->
                    val x = leftPadding + stepX * index - barWidth / 2f
                    val y = topPadding + chartHeight - (value / maxValue) * chartHeight
                    drawRoundRect(
                        brush = Brush.verticalGradient(colors = listOf(primary, primary.copy(alpha = 0.35f))),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, topPadding + chartHeight - y),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(value.toDouble().pretty(), x + barWidth / 2f, y - 12f, pointPaint)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurface.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

package com.artem.fitathlete.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Text(text = title, style = MaterialTheme.typography.headlineMedium)
    if (subtitle != null) {
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
    }
}

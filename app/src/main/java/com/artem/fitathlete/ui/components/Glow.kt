package com.artem.fitathlete.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val resolvedGlow = glowColor ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .blur(28.dp)
                .background(resolvedGlow, RoundedCornerShape(28.dp))
        )
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp) {
            Box(modifier = Modifier.padding(18.dp), content = content)
        }
    }
}

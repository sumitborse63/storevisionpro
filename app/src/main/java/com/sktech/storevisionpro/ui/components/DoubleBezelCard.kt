package com.sktech.storevisionpro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-End "Double-Bezel" (Doppelrand / Nested Architecture) container.
 * Encloses content in an outer bezel shell with hairline border and an inner machined core
 * for depth and tactical hardware feel.
 */
@Composable
fun DoubleBezelCard(
    modifier: Modifier = Modifier,
    outerCornerRadius: Dp = 16.dp,
    innerCornerRadius: Dp = 12.dp,
    shellPadding: Dp = 3.dp,
    shellColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    coreColor: Color = MaterialTheme.colorScheme.surface,
    innerPadding: Dp = 14.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val outerShape = RoundedCornerShape(outerCornerRadius)
    val innerShape = RoundedCornerShape(innerCornerRadius)

    Box(
        modifier = modifier
            .clip(outerShape)
            .background(shellColor)
            .border(width = 1.dp, color = borderColor, shape = outerShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(shellPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(innerShape)
                .background(coreColor)
                .padding(innerPadding)
        ) {
            content()
        }
    }
}

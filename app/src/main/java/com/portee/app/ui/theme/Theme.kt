package com.portee.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Nocturne is a single, fixed dark theme — there is no light variant.
private val PorteeColorScheme = darkColorScheme(
    background = PorteeColors.background,
    surface = PorteeColors.surface,
    primary = PorteeColors.accent,
    onBackground = PorteeColors.text,
    onSurface = PorteeColors.text,
    onPrimary = PorteeColors.background,
)

@Composable
fun PorteeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PorteeColorScheme,
        typography = PorteeTypography,
        content = content,
    )
}

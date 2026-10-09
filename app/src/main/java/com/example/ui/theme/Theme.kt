package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AuraBistroColorScheme = darkColorScheme(
    primary = SaffronGold,
    onPrimary = ObsidianBg,
    primaryContainer = BistroElevated,
    onPrimaryContainer = CopperHighlight,
    secondary = WarmAmber,
    onSecondary = ObsidianBg,
    secondaryContainer = BistroElevated,
    onSecondaryContainer = CreamIvory,
    tertiary = SpatialEmerald,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = CreamIvory,
    surface = BistroSurface,
    onSurface = CreamIvory,
    surfaceVariant = BistroElevated,
    onSurfaceVariant = WarmTaupe,
    outline = BistroCardBorder,
    error = ErrorCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraBistroColorScheme,
        typography = Typography,
        content = content
    )
}

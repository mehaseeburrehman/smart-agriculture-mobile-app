package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AgriPrimary,
    onPrimary = AgriOnPrimary,
    primaryContainer = AgriPrimaryContainer,
    onPrimaryContainer = AgriOnPrimaryContainer,
    secondary = AgriSecondary,
    onSecondary = AgriOnSecondary,
    secondaryContainer = AgriSecondaryContainer,
    onSecondaryContainer = AgriOnSecondaryContainer,
    tertiary = AgriTertiary,
    onTertiary = AgriOnTertiary,
    tertiaryContainer = AgriTertiaryContainer,
    onTertiaryContainer = AgriOnTertiaryContainer,
    background = AgriBackground,
    onBackground = AgriOnBackground,
    surface = AgriSurface,
    onSurface = AgriOnSurface,
    surfaceVariant = AgriSurfaceVariant,
    onSurfaceVariant = AgriOnSurfaceVariant,
    surfaceContainerLowest = AgriSurfaceContainerLowest,
    surfaceContainerLow = AgriSurfaceContainerLow,
    surfaceContainer = AgriSurfaceContainer,
    surfaceContainerHigh = AgriSurfaceContainerHigh,
    surfaceContainerHighest = AgriSurfaceContainerHighest,
    outline = AgriOutline,
    outlineVariant = AgriOutlineVariant,
    error = AgriError,
    onError = AgriOnPrimary,
    errorContainer = AgriErrorContainer,
    onErrorContainer = AgriOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = AgriOnPrimaryContainer,
    onPrimary = AgriPrimary,
    primaryContainer = AgriPrimaryContainer,
    onPrimaryContainer = AgriBackground,
    secondary = AgriSecondaryContainer,
    onSecondary = AgriSecondary,
    secondaryContainer = AgriSecondaryContainer,
    onSecondaryContainer = AgriOnSecondaryContainer,
    background = AgriOnBackground,
    onBackground = AgriBackground,
    surface = AgriOnBackground,
    onSurface = AgriBackground,
    surfaceVariant = AgriOnSurfaceVariant,
    onSurfaceVariant = AgriSurfaceVariant,
    error = AgriError,
    errorContainer = AgriErrorContainer,
    onErrorContainer = AgriOnErrorContainer
)

@Composable
fun SmartAgricultureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SmartAgricultureTheme(darkTheme = darkTheme, content = content)
}


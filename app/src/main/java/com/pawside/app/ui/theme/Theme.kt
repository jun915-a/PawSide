package com.pawside.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = PawAmber,
    onPrimary = PawCharcoal,
    secondary = PawBrown,
    background = PawCharcoal,
    onBackground = PawCream,
    surface = PawCharcoalElevated,
    onSurface = PawCream,
    error = PawGuardRed,
)

private val LightColors = lightColorScheme(
    primary = PawAmberDark,
    onPrimary = PawCream,
    secondary = PawBrown,
    background = PawCream,
    onBackground = PawCharcoal,
    surface = PawCream,
    onSurface = PawCharcoal,
    error = PawGuardRed,
)

@Composable
fun PawsideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = PawsideTypography,
        content = content,
    )
}

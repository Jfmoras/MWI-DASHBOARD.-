package com.moraswood.mwidashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Brown = Color(0xFF2F2118)
private val Brown2 = Color(0xFF5C351F)
private val Cream = Color(0xFFF4F0EB)

private val LightColors = lightColorScheme(
    primary = Brown,
    secondary = Brown2,
    background = Cream
)

private val DarkColors = darkColorScheme(
    primary = Brown2,
    secondary = Brown,
    background = Color(0xFF1C1917)
)

@Composable
fun MWIDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

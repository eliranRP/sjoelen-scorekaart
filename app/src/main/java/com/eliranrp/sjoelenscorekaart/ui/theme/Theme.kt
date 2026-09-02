package com.eliranrp.sjoelenscorekaart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    secondary = Stamp,
    onSecondary = Paper,
    tertiary = InkMuted,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDark,
    onSurfaceVariant = InkMuted,
    outline = Rule,
    error = Stamp,
    onError = Color.White,
)

@Composable
fun SjoelenScorekaartTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}

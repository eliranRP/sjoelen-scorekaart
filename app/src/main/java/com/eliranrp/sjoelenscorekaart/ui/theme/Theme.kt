package com.eliranrp.sjoelenscorekaart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WoodPaperScheme = lightColorScheme(
    primary = WoodMid,
    onPrimary = PaperCream,
    secondary = BrassDeep,
    onSecondary = InkBrown,
    tertiary = VolleBak,
    background = PaperCream,
    onBackground = InkBrown,
    surface = PaperEdge,
    onSurface = InkBrown,
    surfaceVariant = ChipIdle,
    onSurfaceVariant = InkBrown,
    error = MinusRed,
    onError = Color.White,
)

@Composable
fun SjoelenScorekaartTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WoodPaperScheme,
        typography = ScorekaartTypography,
        content = content,
    )
}

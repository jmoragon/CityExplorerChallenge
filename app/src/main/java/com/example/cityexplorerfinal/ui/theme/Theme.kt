package com.example.cityexplorerfinal.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PastelBlue,
    secondary = PastelGreen,
    tertiary = PastelPink,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = TextDarkGray,
    onSecondary = TextDarkGray,
    onBackground = TextDarkGray,
    onSurface = TextDarkGray
)

@Composable
fun CityExplorerFinalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography, // Uses default if Typography.kt is unchanged
        content = content
    )
}
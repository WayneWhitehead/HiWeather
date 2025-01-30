package com.hidesign.hiweather.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimary,
    onPrimary = Color.White,
    secondary = colorAccent,
    onSecondary = Color.White,

    background = colorAccentDark,
    onBackground = Color.White,
    surface = colorAccentDark,
    onSurface = Color.White,

    primaryContainer = Color(0xFF151515),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color.Black.copy(alpha = 0.85f),
    onSecondaryContainer = Color.White,
)

private val shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun HiWeatherTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, view).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        shapes = shapes,
        content = content
    )
}
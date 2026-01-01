package com.example.zenfocus.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SoftTeal,
    secondary = MutedLavender,
    tertiary = PaleBlue,
    background = OledBlack,
    surface = DarkCharcoal,
    onPrimary = OledBlack,
    onSecondary = OledBlack,
    onTertiary = OledBlack,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = DarkPrimary,
    secondary = SoftTeal,
    tertiary = MutedLavender,
    background = LightGray,
    surface = White,
    onPrimary = White,
    onSecondary = DarkText,
    onTertiary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText
)

@Composable
fun ZenFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

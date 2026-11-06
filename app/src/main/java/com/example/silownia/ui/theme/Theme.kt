package com.example.silownia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppPalette = staticCompositionLocalOf { LightPalette }

object AppTheme {
    val colors: AppPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppPalette.current
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkAcc,
    onPrimary = DarkAccInk,
    background = DarkBg,
    onBackground = DarkInk,
    surface = DarkCard,
    onSurface = DarkInk,
    outline = DarkLine
)

private val LightColorScheme = lightColorScheme(
    primary = LightAcc,
    onPrimary = LightAccInk,
    background = LightBg,
    onBackground = LightInk,
    surface = LightCard,
    onSurface = LightInk,
    outline = LightLine
)

@Composable
fun SilowniaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

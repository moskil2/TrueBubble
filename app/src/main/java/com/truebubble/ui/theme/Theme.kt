package com.truebubble.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

data class AppColors(
    val bg: androidx.compose.ui.graphics.Color,
    val surface: androidx.compose.ui.graphics.Color,
    val surface2: androidx.compose.ui.graphics.Color,
    val line: androidx.compose.ui.graphics.Color,
    val text: androidx.compose.ui.graphics.Color,
    val text2: androidx.compose.ui.graphics.Color,
    val text3: androidx.compose.ui.graphics.Color,
    val navBg: androidx.compose.ui.graphics.Color,
    val accentOk: androidx.compose.ui.graphics.Color,
    val onAccent: androidx.compose.ui.graphics.Color,
    val warn: androidx.compose.ui.graphics.Color,
    val error: androidx.compose.ui.graphics.Color,
    val vialTop: androidx.compose.ui.graphics.Color,
    val vialBottom: androidx.compose.ui.graphics.Color,
    val isDark: Boolean,
)

val DarkAppColors = AppColors(
    bg = DarkBg, surface = DarkSurface, surface2 = DarkSurface2,
    line = DarkLine, text = DarkText, text2 = DarkText2, text3 = DarkText3,
    navBg = DarkNavBg, accentOk = AccentOk, onAccent = OnAccent,
    warn = AccentWarn, error = AccentError, vialTop = VialTop, vialBottom = VialBottom,
    isDark = true,
)

val LightAppColors = AppColors(
    bg = LightBg, surface = LightSurface, surface2 = LightBg,
    line = LightLine, text = LightText, text2 = LightText2, text3 = LightText3,
    navBg = LightNavBg, accentOk = AccentOkDark, onAccent = OnAccent,
    warn = AccentWarn, error = AccentError, vialTop = VialTop, vialBottom = VialBottomLight,
    isDark = false,
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val DarkColorScheme = darkColorScheme(
    primary = AccentOk,
    onPrimary = OnAccent,
    background = DarkBg,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = DarkLine,
)

private val LightColorScheme = lightColorScheme(
    primary = AccentOkDark,
    onPrimary = OnAccent,
    background = LightBg,
    surface = LightSurface,
    onBackground = LightText,
    onSurface = LightText,
    outline = LightLine,
)

@Composable
fun TrueBubbleTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

package com.paulchibamba.margin.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSkin = staticCompositionLocalOf { Skins.Ink }

@Composable
fun MarginTheme(skin: Skin = Skins.Paper, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSkin provides skin) {
        MaterialTheme(colorScheme = colorSchemeFor(skin), typography = materialTypography, content = content)
    }
}

private fun colorSchemeFor(skin: Skin): ColorScheme =
    if (skin.isLight) lightColorSchemeFor(skin) else darkColorSchemeFor(skin)

private fun lightColorSchemeFor(skin: Skin) = lightColorScheme(
    primary = MarginColors.InkText,
    onPrimary = MarginColors.White,
    background = skin.background,
    onBackground = skin.content,
    surface = skin.background,
    onSurface = skin.content,
    onSurfaceVariant = skin.mutedContent,
    error = skin.wrong,
)

private fun darkColorSchemeFor(skin: Skin) = darkColorScheme(
    primary = MarginColors.Lime,
    onPrimary = MarginColors.InkText,
    background = skin.background,
    onBackground = skin.content,
    surface = skin.background,
    onSurface = skin.content,
    onSurfaceVariant = skin.mutedContent,
    error = skin.wrong,
)

private val materialTypography = Typography(
    bodyLarge = MarginTypography.body,
    bodyMedium = MarginTypography.bodySmall,
    bodySmall = MarginTypography.caption,
    titleLarge = MarginTypography.screenTitle,
    titleMedium = MarginTypography.conceptTitle,
    labelLarge = MarginTypography.button,
    labelMedium = MarginTypography.label,
    labelSmall = MarginTypography.meta,
)

package com.paulchibamba.margin.feature.appearance

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePalette

@Composable
fun ProvideSurfacePalette(viewModel: AppearanceViewModel = hiltViewModel(), content: @Composable () -> Unit) {
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val palette = SurfacePalette.of(isDark = darkMode.isDark(isSystemInDarkTheme()))
    CompositionLocalProvider(LocalSurfacePalette provides palette, content = content)
}

package com.paulchibamba.margin.feature.appearance

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.designsystem.LocalPostCardStyle
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.PostCardStyle
import com.paulchibamba.margin.designsystem.SurfacePalette

@Composable
fun ProvideAppearance(viewModel: AppearanceViewModel = hiltViewModel(), content: @Composable () -> Unit) {
    val appearance by viewModel.appearance.collectAsStateWithLifecycle()
    CompositionLocalProvider(
        LocalSurfacePalette provides SurfacePalette.of(isDark = appearance.darkMode.isDark(isSystemInDarkTheme())),
        LocalPostCardStyle provides PostCardStyle.of(appearance.isDarkPostsOn),
        content = content,
    )
}

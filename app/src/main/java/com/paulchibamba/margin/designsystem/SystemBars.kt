package com.paulchibamba.margin.designsystem

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat

@Composable
fun StatusBarFollowsSkin() {
    val isLight = LocalSkin.current.isLight
    val window = LocalActivity.current?.window ?: return
    LaunchedEffect(window, isLight) {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isLight
    }
}

@Composable
fun NavigationBarAppearance(isLight: Boolean) {
    val window = LocalActivity.current?.window ?: return
    LaunchedEffect(window, isLight) {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = isLight
    }
}

package com.paulchibamba.margin.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.NavigationBarAppearance
import com.paulchibamba.margin.designsystem.component.MarginNavigationBar
import com.paulchibamba.margin.designsystem.component.NavigationTab
import com.paulchibamba.margin.feature.celebration.CelebrationHost

@Composable
fun MarginApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val selectedTab = destination?.selectedTab()
    NavigationBarAppearance(isLight = destination?.hasDarkBackground() != true)
    Column(Modifier.fillMaxSize().background(MarginColors.Ink)) {
        MarginNavHost(navController, Modifier.weight(1f))
        CelebrationHost(onCelebrate = navController::showCelebration)
        if (selectedTab != null) {
            MarginNavigationBar(
                selectedTab = selectedTab,
                onTabSelect = navController::navigateToTab,
                isLight = selectedTab != NavigationTab.Feed,
            )
        }
    }
}

private fun NavDestination.selectedTab(): NavigationTab? = when {
    hierarchy.any { it.hasRoute<TabGraph.Feed>() } -> NavigationTab.Feed
    hierarchy.any { it.hasRoute<TabGraph.Read>() } -> NavigationTab.Read
    hierarchy.any { it.hasRoute<TabGraph.Settings>() } -> NavigationTab.Settings
    else -> null
}

private fun NavDestination.hasDarkBackground(): Boolean = selectedTab() == NavigationTab.Feed ||
    hasRoute<MarginDestination.Stats>() || hasRoute<MarginDestination.Celebration>()

private fun NavHostController.showCelebration() {
    val destination = currentDestination ?: return
    if (!destination.hasRoute<MarginDestination.Celebration>()) navigate(MarginDestination.Celebration)
}

private fun NavHostController.navigateToTab(tab: NavigationTab) {
    val graph: TabGraph = when (tab) {
        NavigationTab.Feed -> TabGraph.Feed
        NavigationTab.Read -> TabGraph.Read
        NavigationTab.Settings -> TabGraph.Settings
    }
    navigate(graph) {
        popUpTo(this@navigateToTab.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

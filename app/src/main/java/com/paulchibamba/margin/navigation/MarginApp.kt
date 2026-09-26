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

@Composable
fun MarginApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedTab = backStackEntry?.destination?.selectedTab()
    NavigationBarAppearance(isLight = selectedTab != NavigationTab.Feed)
    Column(Modifier.fillMaxSize().background(MarginColors.Ink)) {
        MarginNavHost(navController, Modifier.weight(1f))
        if (selectedTab != null) {
            MarginNavigationBar(
                selectedTab = selectedTab,
                onTabSelect = navController::navigateToTab,
                isLight = selectedTab == NavigationTab.Read,
            )
        }
    }
}

private fun NavDestination.selectedTab(): NavigationTab? = when {
    hierarchy.any { it.hasRoute<TabGraph.Feed>() } -> NavigationTab.Feed
    hierarchy.any { it.hasRoute<TabGraph.Read>() } -> NavigationTab.Read
    else -> null
}

private fun NavHostController.navigateToTab(tab: NavigationTab) {
    val graph: TabGraph = when (tab) {
        NavigationTab.Feed -> TabGraph.Feed
        NavigationTab.Read -> TabGraph.Read
    }
    navigate(graph) {
        popUpTo(this@navigateToTab.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

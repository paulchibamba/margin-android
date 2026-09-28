package com.paulchibamba.margin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.paulchibamba.margin.feature.celebration.CelebrationRoute
import com.paulchibamba.margin.feature.feed.FeedRoute
import com.paulchibamba.margin.feature.read.book.BookRoute
import com.paulchibamba.margin.feature.read.home.ReadHomeRoute
import com.paulchibamba.margin.feature.read.note.NoteRoute
import com.paulchibamba.margin.feature.settings.SettingsRoute
import com.paulchibamba.margin.feature.settings.chapters.ReadingOnlyChaptersRoute
import com.paulchibamba.margin.feature.stats.StatsRoute

@Composable
fun MarginNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = TabGraph.Feed, modifier = modifier) {
        feedGraph(navController)
        readGraph(navController)
        settingsGraph(navController)
        composable<MarginDestination.Note> { NoteRoute(onBack = navController::navigateUp) }
        composable<MarginDestination.Stats> { StatsRoute(onBack = navController::navigateUp) }
        composable<MarginDestination.Celebration> {
            CelebrationRoute(onFinished = navController::closeCelebration)
        }
    }
}

private fun NavGraphBuilder.feedGraph(navController: NavHostController) {
    navigation<TabGraph.Feed>(startDestination = MarginDestination.Feed) {
        composable<MarginDestination.Feed> {
            FeedRoute(onOpenNote = { note, post -> navController.navigate(MarginDestination.Note.of(note, post)) })
        }
    }
}

private fun NavGraphBuilder.readGraph(navController: NavHostController) {
    navigation<TabGraph.Read>(startDestination = MarginDestination.Read) {
        composable<MarginDestination.Read> {
            ReadHomeRoute(
                onOpenBook = { book -> navController.navigate(MarginDestination.Book.of(book)) },
                onOpenNote = { note -> navController.navigate(MarginDestination.Note.of(note)) },
            )
        }
        composable<MarginDestination.Book> {
            BookRoute(
                onBack = navController::navigateUp,
                onOpenNote = { note -> navController.navigate(MarginDestination.Note.of(note)) },
            )
        }
    }
}

private fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation<TabGraph.Settings>(startDestination = MarginDestination.Settings) {
        composable<MarginDestination.Settings> {
            SettingsRoute(
                onOpenReadingOnlyChapters = { navController.navigate(MarginDestination.ReadingOnlyChapters) },
                onOpenStats = { navController.navigate(MarginDestination.Stats) },
            )
        }
        composable<MarginDestination.ReadingOnlyChapters> {
            ReadingOnlyChaptersRoute(onBack = navController::navigateUp)
        }
    }
}

private fun NavHostController.closeCelebration() {
    popBackStack<MarginDestination.Celebration>(inclusive = true)
}

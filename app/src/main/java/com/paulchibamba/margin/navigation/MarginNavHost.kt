package com.paulchibamba.margin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.feature.feed.FeedRoute
import com.paulchibamba.margin.feature.read.book.BookRoute
import com.paulchibamba.margin.feature.read.home.ReadHomeRoute
import com.paulchibamba.margin.feature.read.note.NoteRoute
import com.paulchibamba.margin.placeholder.PlaceholderLink
import com.paulchibamba.margin.placeholder.PlaceholderScreen

@Composable
fun MarginNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = TabGraph.Feed, modifier = modifier) {
        feedGraph(navController)
        readGraph(navController)
        secondaryDestinations(navController)
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
                onOpenSettings = { navController.navigate(MarginDestination.Settings) },
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

private fun NavGraphBuilder.secondaryDestinations(navController: NavHostController) {
    composable<MarginDestination.Note> { NoteRoute(onBack = navController::navigateUp) }
    composable<MarginDestination.Settings> {
        PlaceholderScreen(
            title = "Settings",
            skin = Skins.Paper,
            onBack = navController::navigateUp,
            links = listOf(PlaceholderLink("Stats") { navController.navigate(MarginDestination.Stats) }),
        )
    }
    composable<MarginDestination.Stats> {
        PlaceholderScreen("Stats", Skins.Paper, onBack = navController::navigateUp)
    }
}


package com.paulchibamba.margin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
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
            PlaceholderScreen(
                title = "Feed",
                skin = Skins.Ink,
                links = listOf(
                    PlaceholderLink("Open a note from a post") {
                        navController.navigate(MarginDestination.Note.of(NoteId(PLACEHOLDER_NOTE), PostId("post")))
                    },
                    PlaceholderLink("Settings") { navController.navigate(MarginDestination.Settings) },
                    PlaceholderLink("Stats") { navController.navigate(MarginDestination.Stats) },
                ),
            )
        }
    }
}

private fun NavGraphBuilder.readGraph(navController: NavHostController) {
    navigation<TabGraph.Read>(startDestination = MarginDestination.Read) {
        composable<MarginDestination.Read> {
            PlaceholderScreen(
                title = "Read",
                skin = Skins.Paper,
                links = listOf(
                    PlaceholderLink("Open a book") {
                        navController.navigate(MarginDestination.Book.of(BookSlug(PLACEHOLDER_BOOK)))
                    },
                ),
            )
        }
        composable<MarginDestination.Book> { entry -> BookPlaceholder(entry.toRoute(), navController) }
    }
}

@Composable
private fun BookPlaceholder(book: MarginDestination.Book, navController: NavHostController) {
    PlaceholderScreen(
        title = "Book · ${book.bookSlug.value}",
        skin = Skins.Paper,
        onBack = navController::navigateUp,
        links = listOf(
            PlaceholderLink("Open a note") {
                navController.navigate(MarginDestination.Note.of(NoteId(PLACEHOLDER_NOTE)))
            },
        ),
    )
}

private fun NavGraphBuilder.secondaryDestinations(navController: NavHostController) {
    composable<MarginDestination.Note> { entry ->
        val note = entry.toRoute<MarginDestination.Note>()
        val origin = note.fromPostId?.let { " · from ${it.value}" }.orEmpty()
        PlaceholderScreen("Note$origin", Skins.Paper, onBack = navController::navigateUp)
    }
    composable<MarginDestination.Settings> {
        PlaceholderScreen("Settings", Skins.Paper, onBack = navController::navigateUp)
    }
    composable<MarginDestination.Stats> {
        PlaceholderScreen("Stats", Skins.Paper, onBack = navController::navigateUp)
    }
}

private const val PLACEHOLDER_BOOK = "placeholder-book"
private const val PLACEHOLDER_NOTE = "placeholder-note"

package com.paulchibamba.margin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import com.paulchibamba.margin.feature.celebration.CelebrationRoute
import com.paulchibamba.margin.feature.feed.FeedRoute
import com.paulchibamba.margin.feature.read.book.BookRoute
import com.paulchibamba.margin.feature.read.cover.CoverSearchRoute
import com.paulchibamba.margin.feature.read.home.ReadHomeRoute
import com.paulchibamba.margin.feature.read.note.NoteRoute
import com.paulchibamba.margin.feature.settings.SettingsRoute
import com.paulchibamba.margin.feature.settings.chapters.ReadingOnlyChaptersRoute
import com.paulchibamba.margin.feature.settings.screentime.DoomAppsRoute
import com.paulchibamba.margin.feature.stats.StatsRoute

@Composable
fun MarginNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = TabGraph.Feed, modifier = modifier) {
        feedGraph(navController)
        readGraph(navController)
        settingsGraph(navController)
        composable<MarginDestination.Note> { NoteRoute(onBack = navController::navigateUp) }
        composable<MarginDestination.CoverSearch> { CoverSearchRoute(onClose = navController::navigateUp) }
        composable<MarginDestination.Stats> { StatsRoute(onBack = navController::navigateUp) }
        composable<MarginDestination.Celebration> {
            CelebrationRoute(onFinished = navController::closeCelebration)
        }
    }
}

private fun NavGraphBuilder.feedGraph(navController: NavHostController) {
    navigation<TabGraph.Feed>(startDestination = MarginDestination.Feed) {
        composable<MarginDestination.Feed> {
            FeedRoute(onOpenNote = { note, post -> navController.navigate(noteFromFeed(note, post)) })
        }
    }
}

private fun noteFromFeed(note: NoteId, post: PostId?): MarginDestination.Note {
    val via = if (post == null) NoteOpenVia.CONTINUE else NoteOpenVia.FEED_READ
    return MarginDestination.Note.of(note, via, post)
}

private fun NavGraphBuilder.readGraph(navController: NavHostController) {
    navigation<TabGraph.Read>(startDestination = MarginDestination.Read) {
        composable<MarginDestination.Read> {
            ReadHomeRoute(
                onOpenBook = { book -> navController.navigate(MarginDestination.Book.of(book)) },
                onOpenNote = { note -> navController.navigate(MarginDestination.Note.of(note, NoteOpenVia.CONTINUE)) },
            )
        }
        composable<MarginDestination.Book> { entry ->
            val book = entry.toRoute<MarginDestination.Book>()
            BookRoute(
                onBack = navController::navigateUp,
                onOpenNote = { note -> navController.navigate(MarginDestination.Note.of(note, NoteOpenVia.CHAPTER)) },
                onSearchCover = { title -> navController.navigate(MarginDestination.CoverSearch(book.slug, title)) },
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
                onOpenDoomApps = { navController.navigate(MarginDestination.DoomApps) },
            )
        }
        composable<MarginDestination.ReadingOnlyChapters> {
            ReadingOnlyChaptersRoute(onBack = navController::navigateUp)
        }
        composable<MarginDestination.DoomApps> { DoomAppsRoute(onBack = navController::navigateUp) }
    }
}

private fun NavHostController.closeCelebration() {
    popBackStack<MarginDestination.Celebration>(inclusive = true)
}

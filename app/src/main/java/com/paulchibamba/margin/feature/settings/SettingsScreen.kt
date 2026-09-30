package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.DarkMode

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBookSettingsChange: (BookSettings) -> Unit,
    onRetentionChange: (Double) -> Unit,
    onRetentionChangeFinished: () -> Unit,
    onReviewReminderChange: (Boolean) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
    onDarkPostsChange: (Boolean) -> Unit,
    onOpenReadingOnlyChapters: () -> Unit,
    onOpenStats: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalSurfacePalette.current
    MarginTheme(palette.skin) {
        StatusBarFollowsSkin()
        Column(
            modifier
                .fillMaxSize()
                .background(palette.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsTitle()
            if (!state.isLoading) {
                ActiveBooksSection(state, onBookSettingsChange)
                SectionTitle("Memory", Modifier.padding(start = 4.dp, top = 4.dp))
                RetentionCard(state.retention, onRetentionChange, onRetentionChangeFinished)
                SettingsLinksCard(onOpenReadingOnlyChapters, onOpenStats)
                SectionTitle("Reminders", Modifier.padding(start = 4.dp, top = 4.dp))
                ReviewReminderCard(state.isReviewReminderOn, onReviewReminderChange)
                SectionTitle("Appearance", Modifier.padding(start = 4.dp, top = 4.dp))
                DarkModeCard(state.darkMode, onDarkModeChange)
                DarkPostsCard(state.isDarkPostsOn, onDarkPostsChange)
            }
        }
    }
}

@Composable
private fun SettingsTitle() {
    Box(Modifier.fillMaxWidth().height(52.dp).padding(start = 2.dp), contentAlignment = Alignment.CenterStart) {
        Text(
            "Settings",
            style = MarginTypography.homeTitle,
            color = LocalSurfacePalette.current.text,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Preview(widthDp = 360, heightDp = 980)
@Composable
private fun SettingsScreenPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        SettingsScreen(
            SettingsPreviewData.settings,
            onBookSettingsChange = {},
            onRetentionChange = {},
            onRetentionChangeFinished = {},
            onReviewReminderChange = {},
            onDarkModeChange = {},
            onDarkPostsChange = {},
            onOpenReadingOnlyChapters = {},
            onOpenStats = {},
        )
    }
}

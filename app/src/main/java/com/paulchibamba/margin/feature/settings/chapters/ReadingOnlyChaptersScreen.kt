package com.paulchibamba.margin.feature.settings.chapters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.BookLearningSettings
import com.paulchibamba.margin.feature.settings.SectionTitle
import com.paulchibamba.margin.feature.settings.SettingsCard

@Composable
fun ReadingOnlyChaptersScreen(
    state: ReadingOnlyChaptersUiState,
    onBack: () -> Unit,
    onReadingOnlyChange: (ChapterRef, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalSurfacePalette.current
    MarginTheme(palette.skin) {
        StatusBarFollowsSkin()
        Column(modifier.fillMaxSize().background(palette.background).statusBarsPadding()) {
            BackBar(onBack)
            if (!state.isLoading) BookChapterList(state.books, onReadingOnlyChange)
        }
    }
}

@Composable
private fun BackBar(onBack: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(painterResource(MarginIcons.ArrowBack), "Back", Modifier.size(24.dp), palette.text)
        }
        Text(
            "Reading-only chapters",
            style = MarginTypography.barTitle,
            color = palette.text,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
private fun BookChapterList(books: List<BookLearningSettings>, onReadingOnlyChange: (ChapterRef, Boolean) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Explanation() }
        items(books, key = { it.book.slug.value }) { book -> BookChapters(book, onReadingOnlyChange) }
    }
}

@Composable
private fun Explanation() {
    Text(
        "These chapters get no posts. Reading their notes still moves you forward.",
        style = MarginTypography.detail,
        color = LocalSurfacePalette.current.mutedText,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun BookChapters(book: BookLearningSettings, onReadingOnlyChange: (ChapterRef, Boolean) -> Unit) {
    Column(Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(book.book.title, Modifier.padding(horizontal = 4.dp))
        SettingsCard(contentPadding = PaddingValues(start = 14.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)) {
            book.chapters.forEach { chapter ->
                val chapterRef = ChapterRef(chapter.bookSlug, chapter.number)
                ReadingOnlyChapterRow(chapter, onChange = { isReadingOnly -> onReadingOnlyChange(chapterRef, isReadingOnly) })
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ReadingOnlyChaptersScreenPreview(
    @PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette,
) {
    SurfacePreview(palette) {
        ReadingOnlyChaptersScreen(
            ReadingOnlyChaptersPreviewData.state,
            onBack = {},
            onReadingOnlyChange = { _, _ -> },
        )
    }
}

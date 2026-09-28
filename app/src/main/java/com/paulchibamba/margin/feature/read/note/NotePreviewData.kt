package com.paulchibamba.margin.feature.read.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.usecase.PlaceInChapter
import kotlin.time.Duration.Companion.minutes

object NotePreviewData {
    val fromPost = NoteUiState(
        note = NoteId("alice-bob-appsec/ch04/n005"),
        html = "",
        place = PlaceInChapter(order = 5, noteCount = 8),
        readingTime = 1.minutes,
        isRead = true,
        fromPost = PostId("xss-tip"),
        previous = NoteId("alice-bob-appsec/ch04/n004"),
        next = NoteId("alice-bob-appsec/ch04/n006"),
    )

    val fromBook = fromPost.copy(
        fromPost = null,
        isRead = false,
        place = PlaceInChapter(order = 1, noteCount = 8),
        previous = null,
    )

    @Composable
    fun Body(modifier: Modifier) {
        Column(
            modifier.padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Ch 4 · Cross-site scripting", style = MarginTypography.chip, color = MarginColors.PaperTextFaint)
            Text("Reflected vs stored XSS", style = MarginTypography.prompt, color = MarginColors.InkText)
            Text(
                "A reflected payload rides in on the request and bounces straight back in the response.",
                style = MarginTypography.bookText,
                color = MarginColors.InkText,
            )
        }
    }
}

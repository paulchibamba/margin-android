package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.usecase.ReadingAhead
import com.paulchibamba.margin.feature.feed.notesAheadLabel
import com.paulchibamba.margin.feature.feed.readToUnlockLabel
import kotlin.time.Duration.Companion.minutes

private const val TEASER_MAX_LINES = 4

@Composable
fun PreviewTreatment(
    content: PostContent,
    readingAhead: ReadingAhead,
    onReadAhead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PostColumn(modifier, top = 38.dp) {
        ComingUpChip()
        PostTitle(content.title)
        LockedTeaser(content.readableText.drop(1).joinToString(" "), readingAhead.noteCount)
        ReadToUnlockButton(readingAhead, onReadAhead)
    }
}

@Composable
private fun ComingUpChip() {
    val skin = LocalSkin.current
    Row(
        Modifier.background(skin.content, CircleShape).padding(start = 9.dp, top = 6.dp, end = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.Schedule), null, Modifier.size(16.dp), skin.background)
        Text("Coming up in your reading", style = MarginTypography.chip, color = skin.background)
    }
}

@Composable
private fun LockedTeaser(teaser: String, noteCount: Int) {
    Box(Modifier.widthIn(max = 260.dp), contentAlignment = Alignment.Center) {
        Text(
            teaser,
            Modifier.blur(5.dp).clearAndSetSemantics {},
            style = MarginTypography.teaser,
            color = LocalSkin.current.content,
            maxLines = TEASER_MAX_LINES,
            overflow = TextOverflow.Clip,
        )
        LockPill(noteCount)
    }
}

@Composable
private fun LockPill(noteCount: Int) {
    Row(
        Modifier
            .background(MarginColors.White.copy(alpha = 0.85f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.Lock), null, Modifier.size(17.dp), MarginColors.InkText)
        Text(notesAheadLabel(noteCount), style = MarginTypography.smallButton, color = MarginColors.InkText)
    }
}

@Composable
private fun ReadToUnlockButton(readingAhead: ReadingAhead, onClick: () -> Unit) {
    val skin = LocalSkin.current
    Row(
        Modifier
            .background(skin.content, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(readToUnlockLabel(readingAhead.readingTime), style = MarginTypography.button, color = skin.background)
        Icon(painterResource(MarginIcons.ArrowForward), null, Modifier.size(20.dp), skin.background)
    }
}

private val previewVersus = PostContent.Versus(
    title = "One kind of XSS never touches your server.",
    left = "DOM-based XSS runs entirely in the browser: the payload flows from location.hash into innerHTML",
    right = "and never reaches the server logs.",
)

private val previewAhead = ReadingAhead(NoteId("alice-bob-appsec/ch04/n002"), noteCount = 3, readingTime = 3.minutes)

@Composable
private fun PreviewTreatmentPreviewOn(skin: Skin) =
    PostPagePreview(skin, previewVersus, CandidateSource.PREVIEW, previewAhead)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun PreviewTreatmentEmberPreview() = PreviewTreatmentPreviewOn(Skins.Ember)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun PreviewTreatmentPaperPreview() = PreviewTreatmentPreviewOn(Skins.Paper)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun PreviewTreatmentInkPreview() = PreviewTreatmentPreviewOn(Skins.Ink)

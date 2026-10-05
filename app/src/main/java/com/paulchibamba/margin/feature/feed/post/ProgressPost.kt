package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.feature.feed.iconOf
import com.paulchibamba.margin.feature.feed.label

@Composable
fun ProgressPost(content: PostContent.Progress, onReadNote: () -> Unit, modifier: Modifier = Modifier) {
    PostColumn(modifier, top = 38.dp, spacing = 18.dp) {
        KindChip(content.kind)
        PostTitle(content.title, style = headlineStyleFor(content.title))
        if (content.kind == RewardKind.Quote) {
            QuoteBlock(content.body, content.sourceLine)
        } else {
            PostParagraph(content.body)
        }
        if (content.kind == RewardKind.ComingUp && content.noteId != null) ReadNoteLink(onReadNote)
    }
}

@Composable
private fun KindChip(kind: RewardKind) {
    val skin = LocalSkin.current
    Row(
        Modifier.background(skin.content, CircleShape).padding(start = 9.dp, top = 6.dp, end = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(kind.iconOf()), null, Modifier.size(16.dp), skin.background)
        Text(kind.label(), style = MarginTypography.chip, color = skin.background)
    }
}

@Composable
private fun QuoteBlock(quote: String, sourceLine: String?) {
    val skin = LocalSkin.current
    Row(Modifier.height(IntrinsicSize.Min).widthIn(max = 300.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(skin.content, CircleShape))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(quote, style = MarginTypography.bookText, color = skin.content)
            sourceLine?.let { source -> Text(source, style = MarginTypography.detail, color = skin.mutedContent) }
        }
    }
}

@Composable
private fun ReadNoteLink(onRead: () -> Unit) {
    val skin = LocalSkin.current
    Row(
        Modifier.clickable(role = Role.Button, onClick = onRead).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Read on", style = MarginTypography.button, color = skin.content)
        Icon(painterResource(MarginIcons.ArrowForward), null, Modifier.size(20.dp), skin.content)
    }
}

private val previewComeback = PostContent.Progress(
    kind = RewardKind.Comeback,
    title = "Least Privilege, revisited",
    body = "25 Sep: Least Privilege beat you 2×. Since then you have got it right 3 times in a row.",
    sourceLine = null,
    noteId = null,
)

private val previewQuote = PostContent.Progress(
    kind = RewardKind.Quote,
    title = "From Least Privilege",
    body = "Access that is never granted can never be abused by an attacker who takes over the account.",
    sourceLine = "Alice and Bob Learn Application Security · Least Privilege",
    noteId = NoteId("alice-bob-appsec/ch01/n004"),
)

private val previewComingUp = PostContent.Progress(
    kind = RewardKind.ComingUp,
    title = "Coming up: Need to Know",
    body = "Read on. In about 3 notes, Access Models brings in Need to Know, and it builds on Least Privilege.",
    sourceLine = null,
    noteId = NoteId("alice-bob-appsec/ch01/n006"),
)

private val previewReExplain = PostContent.Progress(
    kind = RewardKind.ReExplain,
    title = "Least privilege, the Android way",
    body = "Your app asks for CAMERA when the user taps scan, not every permission at install. Least privilege " +
        "is that rule for every account and service: the narrowest access, only for as long as it is needed.",
    sourceLine = null,
    noteId = null,
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ComebackPostInkPreview() = PostPagePreview(Skins.Ink, previewComeback)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun QuotePostPaperPreview() = PostPagePreview(Skins.Paper, previewQuote)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ComingUpPostForestPreview() = PostPagePreview(Skins.Forest, previewComingUp)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ReExplainPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewReExplain)

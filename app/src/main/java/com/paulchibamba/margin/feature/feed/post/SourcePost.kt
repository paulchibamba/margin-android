package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun SourcePost(content: PostContent.Source, onRead: () -> Unit, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    Column(
        modifier.fillMaxSize().testTag(POST_BODY_TAG).padding(start = 22.dp, top = 40.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FromTheBookChip()
        PostTitle(content.title)
        Text(
            content.excerpt,
            Modifier.weight(1f, fill = false),
            style = MarginTypography.bookText,
            color = skin.content,
            overflow = TextOverflow.Ellipsis,
        )
        ReadLink(onRead)
    }
}

@Composable
private fun FromTheBookChip() {
    val skin = LocalSkin.current
    Row(
        Modifier.background(skin.surface, CircleShape).padding(start = 9.dp, top = 6.dp, end = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.AutoStories), null, Modifier.size(16.dp), skin.content)
        Text("From the book", style = MarginTypography.chip, color = skin.content)
    }
}

@Composable
private fun ReadLink(onRead: () -> Unit) {
    val skin = LocalSkin.current
    Row(
        Modifier.clickable(role = Role.Button, onClick = onRead).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Read the note", style = MarginTypography.button, color = skin.content)
        Icon(painterResource(MarginIcons.ArrowForward), null, Modifier.size(20.dp), skin.content)
    }
}

private val previewSource = PostContent.Source(
    title = "Least privilege",
    excerpt = "Give every user, service and process only the access it needs to do its job, and nothing more. " +
        "When something is compromised, the damage stops at the edge of what it was allowed to touch. " +
        "Review those permissions regularly, because access has a way of growing quietly over time.",
    noteId = NoteId("alice-bob-appsec/ch01/n004"),
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun SourcePostPaperPreview() = PostPagePreview(Skins.Paper, previewSource)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun SourcePostInkPreview() = PostPagePreview(Skins.Ink, previewSource)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun SourcePostForestPreview() = PostPagePreview(Skins.Forest, previewSource)

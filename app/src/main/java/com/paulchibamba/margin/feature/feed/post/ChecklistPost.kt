package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.paulchibamba.margin.domain.model.PostContent

private val TickBoxShape = RoundedCornerShape(6.dp)

@Composable
fun ChecklistPost(content: PostContent.Checklist, onEngaged: () -> Unit, modifier: Modifier = Modifier) {
    var ticked by rememberSaveable(content) { mutableStateOf(emptyList<Int>()) }
    PostColumn(modifier) {
        PostTitle(content.title)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            content.items.forEachIndexed { index, item ->
                ChecklistItem(item, isTicked = index in ticked) { isTicked ->
                    ticked = if (isTicked) ticked + index else ticked - index
                    if (isTicked) onEngaged()
                }
            }
        }
    }
}

@Composable
private fun ChecklistItem(text: String, isTicked: Boolean, onTickedChange: (Boolean) -> Unit) {
    val skin = LocalSkin.current
    Row(
        Modifier
            .widthIn(max = 290.dp)
            .toggleable(isTicked, role = Role.Checkbox, onValueChange = onTickedChange)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        TickBox(isTicked)
        Text(text, style = MarginTypography.bubble, color = if (isTicked) skin.mutedContent else skin.content)
    }
}

@Composable
private fun TickBox(isTicked: Boolean) {
    val skin = LocalSkin.current
    val box = Modifier.padding(top = 1.dp).size(18.dp)
    if (isTicked) {
        Box(box.background(skin.correct, TickBoxShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(MarginIcons.Check), null, Modifier.size(14.dp), skin.background)
        }
    } else {
        Box(box.border(2.dp, skin.mutedContent, TickBoxShape))
    }
}

private val previewChecklist = PostContent.Checklist(
    title = "Before you ship a login form",
    items = listOf(
        "Rate-limit attempts per account and per IP.",
        "Return the same error for a wrong user and a wrong password.",
        "Hash passwords with a slow, salted algorithm.",
        "Offer a second factor.",
    ),
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ChecklistPostForestPreview() = PostPagePreview(Skins.Forest, previewChecklist)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ChecklistPostPaperPreview() = PostPagePreview(Skins.Paper, previewChecklist)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ChecklistPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewChecklist)

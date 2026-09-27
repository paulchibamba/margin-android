package com.paulchibamba.margin.feature.feed.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private val CodeCardShape = RoundedCornerShape(18.dp)

@Composable
fun CodeExamplePost(content: PostContent.CodeExample, modifier: Modifier = Modifier) {
    PostColumn(modifier) {
        PostTitle(content.title)
        CodeCard(content.code, CodeVerdict.of(content.title, content.caption))
        content.caption?.let { caption -> PostParagraph(caption, style = MarginTypography.bodySmall) }
    }
}

@Composable
private fun CodeCard(code: String, verdict: CodeVerdict?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MarginColors.InkText, CodeCardShape)
            .border(1.dp, MarginColors.White.copy(alpha = 0.08f), CodeCardShape)
            .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        verdict?.let { VerdictTag(it) }
        Text(
            code,
            Modifier.horizontalScroll(rememberScrollState()),
            style = MarginTypography.code,
            color = MarginColors.CodeText,
            softWrap = false,
        )
    }
}

@Composable
private fun VerdictTag(verdict: CodeVerdict) {
    val (label, color, icon) = when (verdict) {
        CodeVerdict.SAFE -> Triple("Safe", MarginColors.Lime, MarginIcons.CheckCircle)
        CodeVerdict.UNSAFE -> Triple("Unsafe", MarginColors.Wrong, MarginIcons.Cancel)
    }
    TagChip(label, color, icon)
}

@Composable
private fun TagChip(label: String, color: Color, @DrawableRes icon: Int) {
    Row(
        Modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
            .padding(start = 5.dp, top = 3.dp, end = 8.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(icon), null, Modifier.size(14.dp), color)
        Text(label, style = MarginTypography.railLabel, color = color)
    }
}

private val previewCode = PostContent.CodeExample(
    title = "Bind it. Don't build it.",
    code = "db.query(\n  \"SELECT * FROM users\n   WHERE id = ?\", id)",
    caption = "Parameters keep data as data, so the query's shape can't change, and it stays safe.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostPaperPreview() = PostPagePreview(Skins.Paper, previewCode)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostInkPreview() = PostPagePreview(Skins.Ink, previewCode)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostForestPreview() = PostPagePreview(Skins.Forest, previewCode)

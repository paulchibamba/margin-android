package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins

private val SnackbarShape = RoundedCornerShape(14.dp)

@Composable
fun FeedSnackbar(
    message: String,
    detail: String?,
    actionLabel: String?,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .shadow(12.dp, SnackbarShape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(MarginColors.InkSnackbar, SnackbarShape)
            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SnackbarText(message, detail, Modifier.weight(1f))
        if (actionLabel != null) SnackbarAction(actionLabel, onAction)
    }
}

@Composable
private fun SnackbarText(message: String, detail: String?, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(message, style = MarginTypography.snackbarMessage, color = MarginColors.White)
        if (detail != null) {
            Text(
                detail,
                style = MarginTypography.snackbarDetail,
                color = MarginColors.White.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SnackbarAction(label: String, onClick: () -> Unit) {
    Text(
        label,
        style = MarginTypography.snackbarAction,
        color = MarginColors.Lime,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    )
}

@Composable
private fun FeedSnackbarPreviewOn(background: Color) {
    Box(Modifier.background(background).padding(12.dp)) {
        FeedSnackbar(
            message = "You'll see this again in ~10 min",
            detail = "Alice & Bob · Ch 4 · 15/48",
            actionLabel = "Read page",
            onAction = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(widthDp = 360)
@Composable
private fun FeedSnackbarInkPreview() = FeedSnackbarPreviewOn(Skins.Ink.background)

@Preview(widthDp = 360)
@Composable
private fun FeedSnackbarPaperPreview() = FeedSnackbarPreviewOn(Skins.Paper.background)

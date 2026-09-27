package com.paulchibamba.margin.feature.read.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

private const val KNOW_THIS_LABEL = "I know this"

@Composable
fun SwipeableChapterRow(
    row: ChapterRowState,
    onOpenNote: () -> Unit,
    onMarkKnown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    key(row.chapter, row.isDone) {
        val state = rememberSwipeToDismissBoxState()
        SwipeToDismissBox(
            state = state,
            backgroundContent = { KnowThisBackground() },
            modifier = modifier.semantics { customActions = knowThisActions(row, onMarkKnown) },
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = !row.isDone,
            onDismiss = { value -> if (value == SwipeToDismissBoxValue.EndToStart) onMarkKnown() },
        ) {
            ChapterRow(row, onOpenNote)
        }
    }
}

private fun knowThisActions(row: ChapterRowState, onMarkKnown: () -> Unit): List<CustomAccessibilityAction> =
    if (row.isDone) emptyList() else listOf(CustomAccessibilityAction(KNOW_THIS_LABEL) { onMarkKnown(); true })

@Composable
private fun KnowThisBackground() {
    Row(
        Modifier.fillMaxSize().background(MarginColors.CorrectOnLight).padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.DoneAll), null, Modifier.size(20.dp), MarginColors.White)
        Text(KNOW_THIS_LABEL, style = MarginTypography.smallButton, color = MarginColors.White)
    }
}

@Preview(widthDp = 360, heightDp = 64)
@Composable
private fun KnowThisBackgroundPreview() {
    KnowThisBackground()
}

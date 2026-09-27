package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.Confidence

private val railStates = listOf(
    ActionRailState(),
    ActionRailState(confidence = Confidence.GOT, disabledActions = setOf(PostAction.LOST)),
    ActionRailState(confidence = Confidence.LOST, disabledActions = setOf(PostAction.GOT)),
    ActionRailState(isSaved = true, isMarkedLess = true),
)

@Composable
private fun ActionRailsOn(skin: Skin) {
    MarginTheme(skin) {
        Row(Modifier.background(skin.background).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            railStates.forEach { ActionRail(state = it, onAction = {}) }
        }
    }
}

@Preview
@Composable
private fun ActionRailInkPreview() = ActionRailsOn(Skins.Ink)

@Preview
@Composable
private fun ActionRailPaperPreview() = ActionRailsOn(Skins.Paper)

@Preview
@Composable
private fun ActionRailEmberPreview() = ActionRailsOn(Skins.Ember)

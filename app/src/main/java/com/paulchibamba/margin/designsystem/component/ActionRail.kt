package com.paulchibamba.margin.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulchibamba.margin.designsystem.CHROME_FONT_SCALE_CAP
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.shrinkFor
import com.paulchibamba.margin.designsystem.withFontScaleCap
import com.paulchibamba.margin.domain.actions.PostAction

private const val ACTIVE_SCALE = 1.14f
private const val DISABLED_ALPHA = 0.35f
private val GLYPH_SIZE = 31.sp

@Composable
fun ActionRail(state: ActionRailState, onAction: (PostAction) -> Unit, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    val colors = FeedChromeColors.of(skin.isLight)
    val haptics = LocalHapticFeedback.current
    Column(
        modifier.width(56.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PostAction.entries.forEach { action ->
            val isActive = state.isActive(action)
            RailItem(action, isActive, state.isEnabled(action), colors, skin.activeColorOf(action)) {
                if (action == PostAction.SAVE && !isActive) haptics.tick()
                onAction(action)
            }
        }
    }
}

@Composable
private fun RailItem(
    action: PostAction,
    isActive: Boolean,
    isEnabled: Boolean,
    colors: FeedChromeColors,
    activeColor: Color,
    onClick: () -> Unit,
) {
    val tint = if (isActive) activeColor else colors.content
    val scale by animateFloatAsState(if (isActive) ACTIVE_SCALE else 1f, label = "rail item scale")
    Column(
        Modifier
            .alpha(if (isEnabled) 1f else DISABLED_ALPHA)
            .clickable(enabled = isEnabled, role = Role.Button, onClick = onClick)
            .semantics { selected = isActive },
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val glyphSize = with(LocalDensity.current) { GLYPH_SIZE.toDp() * shrinkFor(fontScale, CHROME_FONT_SCALE_CAP) }
        Icon(painterResource(iconOf(action, isActive)), labelOf(action), Modifier.size(glyphSize).scale(scale), tint)
        val style = MarginTypography.railLabel.withFontScaleCap().withChromeShadow(colors, 8.dp, 0.35f)
        Text(labelOf(action), style = style, color = tint, maxLines = 1, softWrap = false,
            modifier = Modifier.clearAndSetSemantics {})
    }
}

private fun HapticFeedback.tick() = performHapticFeedback(HapticFeedbackType.ContextClick)

private fun Skin.activeColorOf(action: PostAction): Color = when (action) {
    PostAction.GOT -> correct
    PostAction.LOST -> wrong
    PostAction.SAVE -> save
    PostAction.READ, PostAction.LESS -> content
}

@DrawableRes
private fun iconOf(action: PostAction, isActive: Boolean): Int = when (action) {
    PostAction.GOT -> MarginIcons.ThumbUp
    PostAction.LOST -> MarginIcons.SentimentStressed
    PostAction.READ -> MarginIcons.MenuBook
    PostAction.SAVE -> MarginIcons.Bookmark
    PostAction.LESS -> if (isActive) MarginIcons.Block else MarginIcons.BlockOutlined
}

internal fun labelOf(action: PostAction): String = when (action) {
    PostAction.GOT -> "Got it"
    PostAction.LOST -> "Lost"
    PostAction.READ -> "Read"
    PostAction.SAVE -> "Save"
    PostAction.LESS -> "Less"
}

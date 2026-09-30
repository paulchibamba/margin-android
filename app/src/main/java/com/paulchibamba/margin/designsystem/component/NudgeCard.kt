package com.paulchibamba.margin.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalPostCardStyle
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.PostCardStyle
import com.paulchibamba.margin.designsystem.Skins

private val CardShape = RoundedCornerShape(20.dp)

@Composable
fun NudgeCard(
    title: String,
    detail: String,
    primaryAction: NudgeAction,
    secondaryAction: NudgeAction?,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = MarginIcons.Autorenew,
) {
    val colors = NudgeCardColors.of(LocalPostCardStyle.current)
    Column(
        modifier
            .shadow(16.dp, CardShape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(colors.background, CardShape)
            .padding(16.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NudgeMessage(icon, title, detail, colors)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryNudgeButton(primaryAction, colors)
            if (secondaryAction != null) SecondaryNudgeButton(secondaryAction, colors)
        }
    }
}

@Composable
private fun NudgeMessage(@DrawableRes icon: Int, title: String, detail: String, colors: NudgeCardColors) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(painterResource(icon), contentDescription = null, Modifier.size(22.dp), colors.icon)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MarginTypography.cardTitle, color = colors.title)
            Text(detail, style = MarginTypography.detail, color = colors.detail)
        }
    }
}

@Composable
private fun PrimaryNudgeButton(action: NudgeAction, colors: NudgeCardColors) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(colors.button)
            .clickable(role = Role.Button, onClick = action.onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        action.icon?.let { Icon(painterResource(it), null, Modifier.size(17.dp), colors.onButton) }
        Text(action.label, style = MarginTypography.smallButton, color = colors.onButton)
    }
}

@Composable
private fun SecondaryNudgeButton(action: NudgeAction, colors: NudgeCardColors) {
    Text(
        action.label,
        style = MarginTypography.smallButton,
        color = colors.secondaryButton,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = action.onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
    )
}

@Composable
private fun NudgeCardPreviewOn(background: Color) {
    Box(Modifier.background(background).padding(12.dp).width(290.dp)) {
        NudgeCard(
            title = "Got it. A different angle is next.",
            detail = "No quiz on CORS until it clicks.",
            primaryAction = NudgeAction("Book's words", MarginIcons.MenuBook) {},
            secondaryAction = NudgeAction("Keep going") {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun NudgeCardCobaltPreview() = NudgeCardPreviewOn(Skins.Cobalt.background)

@Preview
@Composable
private fun NudgeCardInkPreview() = NudgeCardPreviewOn(Skins.Ink.background)

@Preview
@Composable
private fun NudgeCardDarkPreview() {
    CompositionLocalProvider(LocalPostCardStyle provides PostCardStyle.DARK) {
        NudgeCardPreviewOn(Skins.Ink.background)
    }
}

@Preview
@Composable
private fun NudgeCardPaperPreview() = NudgeCardPreviewOn(Skins.Paper.background)

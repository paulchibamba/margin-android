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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
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
    Column(
        modifier
            .shadow(16.dp, CardShape, ambientColor = Color.Black, spotColor = Color.Black)
            .background(MarginColors.PaperCard, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NudgeMessage(icon, title, detail)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryNudgeButton(primaryAction)
            if (secondaryAction != null) SecondaryNudgeButton(secondaryAction)
        }
    }
}

@Composable
private fun NudgeMessage(@DrawableRes icon: Int, title: String, detail: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(painterResource(icon), contentDescription = null, Modifier.size(22.dp), MarginColors.Cobalt)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MarginTypography.cardTitle, color = MarginColors.InkText)
            Text(detail, style = MarginTypography.detail, color = MarginColors.PaperTextMuted)
        }
    }
}

@Composable
private fun PrimaryNudgeButton(action: NudgeAction) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(MarginColors.InkText)
            .clickable(role = Role.Button, onClick = action.onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        action.icon?.let { Icon(painterResource(it), null, Modifier.size(17.dp), MarginColors.White) }
        Text(action.label, style = MarginTypography.smallButton, color = MarginColors.White)
    }
}

@Composable
private fun SecondaryNudgeButton(action: NudgeAction) {
    Text(
        action.label,
        style = MarginTypography.smallButton,
        color = MarginColors.InkText,
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
private fun NudgeCardPaperPreview() = NudgeCardPreviewOn(Skins.Paper.background)

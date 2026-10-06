package com.paulchibamba.margin.feature.drop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.component.FeedTopBar
import com.paulchibamba.margin.designsystem.component.SegmentProgress
import com.paulchibamba.margin.designsystem.component.TopBarDrop
import com.paulchibamba.margin.domain.drop.DropCompletion
import com.paulchibamba.margin.domain.drop.DropEvidence

@Composable
fun DropCompletionPage(
    completion: DropCompletion?,
    streak: Int,
    size: Int,
    onKeepGoing: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MarginTheme(Skins.Ink) {
        Column(modifier.fillMaxSize().background(Skins.Ink.background).statusBarsPadding()) {
            val segments = SegmentProgress(count = size.coerceAtLeast(1), current = size.coerceAtLeast(1))
            FeedTopBar(streak, null, onMoreClick = {}, drop = TopBarDrop.Progress("Drop · done", segments, onClose))
            Column(
                Modifier.padding(start = 24.dp, top = 70.dp, end = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                StreakFlame(completion?.streak ?: streak)
                Text("Today's drop, done.", style = MarginTypography.screenTitle, color = MarginColors.White)
                completion?.let { done -> EvidenceLine(DropLabels.evidence(done.evidence)) }
                KeepGoingButton(onKeepGoing)
            }
        }
    }
}

@Composable
private fun StreakFlame(streak: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(MarginIcons.LocalFireDepartment), null, Modifier.size(44.dp), MarginColors.Streak)
        Text("$streak", style = MarginTypography.screenTitle, color = MarginColors.White)
    }
}

@Composable
private fun EvidenceLine(text: String) {
    Text(text, style = MarginTypography.bodySmall, color = MarginColors.White.copy(alpha = 0.72f))
}

@Composable
private fun KeepGoingButton(onClick: () -> Unit) {
    Row(
        Modifier.padding(top = 14.dp).fillMaxWidth().clip(CircleShape).background(MarginColors.Lime)
            .clickable(role = Role.Button, onClick = onClick).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Keep going", style = MarginTypography.button, color = MarginColors.InkText)
        Icon(painterResource(MarginIcons.ArrowForward), null, Modifier.size(20.dp), MarginColors.InkText)
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun DropCompletionPagePreview() {
    DropCompletionPage(DropCompletion(4, DropEvidence.Remembered(2)), streak = 3, size = 6, {}, {})
}

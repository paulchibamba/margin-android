package com.paulchibamba.margin.feature.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.drop.LearnedSlot
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private val ButtonShape = RoundedCornerShape(8.dp)

@Composable
fun DebugToolsSection(
    state: DebugToolsUiState,
    onAdvance: (Duration) -> Unit,
    onResetClock: () -> Unit,
    onShowDueCount: () -> Unit,
    onSendReviewReminder: () -> Unit,
    onShowEvents: () -> Unit,
    onBakeProgressPosts: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Debug · ${state.clockLabel}",
            style = MarginTypography.sectionTitle,
            color = MarginColors.White.copy(alpha = 0.7f),
            modifier = Modifier.semantics { heading() },
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DebugButton("+1 hour") { onAdvance(1.hours) }
            DebugButton("+1 day") { onAdvance(1.days) }
            DebugButton("Reset clock", onClick = onResetClock)
            DebugButton("Show due count", onClick = onShowDueCount)
            DebugButton("Send reminder", onClick = onSendReviewReminder)
            DebugButton("Show events", onClick = onShowEvents)
            DebugButton("Bake now", onClick = onBakeProgressPosts)
            DebugButton(state.resetProgressLabel, state.isProgressResetArmed, onResetProgress)
        }
        listOfNotNull(state.dueCountLabel, state.bakeReport, state.dropSlotLabel).forEach { label ->
            Text(label, style = MarginTypography.label, color = MarginColors.Lime)
        }
        if (state.bakeLines.isNotEmpty()) LineList(state.bakeLines)
        state.recentEvents?.let { lines -> RecentEventList(lines) }
    }
}

@Composable
private fun RecentEventList(lines: List<String>) {
    if (lines.isEmpty()) Text("No events yet", style = MarginTypography.label, color = MarginColors.Lime)
    LineList(lines)
}

@Composable
private fun LineList(lines: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            Text(line, style = MarginTypography.chip, color = MarginColors.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun DebugButton(label: String, isWarning: Boolean = false, onClick: () -> Unit) {
    Box(
        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MarginTypography.chip,
            color = if (isWarning) MarginColors.Wrong else MarginColors.White,
            modifier = Modifier.background(MarginColors.White.copy(alpha = 0.1f), ButtonShape)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        )
    }
}

@Preview(widthDp = 360)
@Composable
private fun DebugToolsSectionPreview() {
    Box(Modifier.background(MarginColors.InkSheet).padding(20.dp)) {
        DebugToolsSection(
            state = DebugToolsUiState(
                offset = 2.days,
                dueCount = 3,
                dropSlot = LearnedSlot(LocalTime.of(21, 0), sessionsInSlot = 4, sessionsCounted = 11),
                recentEvents = listOf("21:04:05 session_start {\"entry\":\"launcher\"}"),
            ),
            onAdvance = {},
            onResetClock = {},
            onShowDueCount = {},
            onSendReviewReminder = {},
            onShowEvents = {},
            onBakeProgressPosts = {},
            onResetProgress = {},
        )
    }
}

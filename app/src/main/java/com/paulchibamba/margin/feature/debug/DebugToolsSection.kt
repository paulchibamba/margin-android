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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private val ButtonShape = RoundedCornerShape(8.dp)

@Composable
fun DebugToolsSection(
    state: DebugClockUiState,
    onAdvance: (Duration) -> Unit,
    onReset: () -> Unit,
    onShowDueCount: () -> Unit,
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
            DebugButton("Reset clock", onReset)
            DebugButton("Show due count", onShowDueCount)
        }
        state.dueCountLabel?.let { label ->
            Text(label, style = MarginTypography.label, color = MarginColors.Lime)
        }
    }
}

@Composable
private fun DebugButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MarginTypography.chip,
            color = MarginColors.White,
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
            state = DebugClockUiState(offset = 2.days, dueCount = 3),
            onAdvance = {},
            onReset = {},
            onShowDueCount = {},
        )
    }
}

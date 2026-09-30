package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.feature.read.label

@Composable
fun PrioritySelector(selected: Priority, onSelect: (Priority) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        Priority.entries.forEachIndexed { index, priority ->
            SegmentedButton(
                selected = priority == selected,
                onClick = { onSelect(priority) },
                shape = SegmentedButtonDefaults.itemShape(index, Priority.entries.size),
                colors = settingsSegmentColors(),
                icon = {},
            ) {
                Text(priority.label(), style = MarginTypography.smallButton)
            }
        }
    }
}

@Preview(widthDp = 328, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun PrioritySelectorPreview() {
    PrioritySelector(selected = Priority.MAIN, onSelect = {})
}

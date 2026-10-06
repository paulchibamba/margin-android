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

@Composable
fun <T> SettingsSegments(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                colors = settingsSegmentColors(),
                icon = {},
            ) {
                Text(label(option), style = MarginTypography.smallButton)
            }
        }
    }
}

@Preview(widthDp = 328, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun SettingsSegmentsPreview() {
    SettingsSegments(listOf("One", "Two", "Three"), selected = "Two", label = { it }, onSelect = {})
}

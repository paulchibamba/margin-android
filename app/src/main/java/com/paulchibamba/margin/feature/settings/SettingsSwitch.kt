package com.paulchibamba.margin.feature.settings

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.MarginColors

private const val DISABLED_ALPHA = 0.38f

@Composable
fun SettingsSwitch(isChecked: Boolean, isEnabled: Boolean, modifier: Modifier = Modifier) {
    Switch(isChecked, onCheckedChange = null, modifier, enabled = isEnabled, colors = switchColors())
}

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = MarginColors.Lime,
    checkedTrackColor = MarginColors.InkText,
    checkedBorderColor = MarginColors.InkText,
    uncheckedThumbColor = MarginColors.PaperTextAhead,
    uncheckedTrackColor = Color.Transparent,
    uncheckedBorderColor = MarginColors.PaperTextAhead,
    disabledCheckedThumbColor = MarginColors.Lime,
    disabledCheckedTrackColor = MarginColors.InkText.copy(alpha = DISABLED_ALPHA),
    disabledCheckedBorderColor = Color.Transparent,
    disabledUncheckedThumbColor = MarginColors.PaperTextAhead.copy(alpha = DISABLED_ALPHA),
    disabledUncheckedTrackColor = Color.Transparent,
    disabledUncheckedBorderColor = MarginColors.PaperTextAhead.copy(alpha = DISABLED_ALPHA),
)

package com.paulchibamba.margin.feature.settings

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePalette

private const val DISABLED_ALPHA = 0.38f

@Composable
fun SettingsSwitch(isChecked: Boolean, isEnabled: Boolean, modifier: Modifier = Modifier) {
    Switch(isChecked, onCheckedChange = null, modifier, enabled = isEnabled, colors = switchColors())
}

@Composable
private fun switchColors(palette: SurfacePalette = LocalSurfacePalette.current) = SwitchDefaults.colors(
    checkedThumbColor = palette.checkedSwitchThumb,
    checkedTrackColor = palette.accent,
    checkedBorderColor = palette.accent,
    uncheckedThumbColor = palette.aheadText,
    uncheckedTrackColor = Color.Transparent,
    uncheckedBorderColor = palette.aheadText,
    disabledCheckedThumbColor = palette.checkedSwitchThumb,
    disabledCheckedTrackColor = palette.accent.copy(alpha = DISABLED_ALPHA),
    disabledCheckedBorderColor = Color.Transparent,
    disabledUncheckedThumbColor = palette.aheadText.dimmed(),
    disabledUncheckedTrackColor = Color.Transparent,
    disabledUncheckedBorderColor = palette.aheadText.dimmed(),
)

private fun Color.dimmed() = copy(alpha = alpha * DISABLED_ALPHA)

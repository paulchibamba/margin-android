package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.model.DarkMode

@Composable
fun DarkModeCard(mode: DarkMode, onChange: (DarkMode) -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    SettingsCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Dark theme", style = MarginTypography.settingTitle, color = palette.text)
            Text(
                "Always keeps every screen dark. System follows your phone.",
                style = MarginTypography.label,
                color = palette.faintText,
            )
        }
        DarkModeSelector(mode, onChange)
    }
}

@Composable
private fun DarkModeSelector(selected: DarkMode, onSelect: (DarkMode) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        DarkMode.entries.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, DarkMode.entries.size),
                colors = settingsSegmentColors(),
                icon = {},
            ) {
                Text(darkModeLabel(mode), style = MarginTypography.smallButton)
            }
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun DarkModeCardPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        DarkModeCard(DarkMode.ALWAYS, onChange = {}, modifier = Modifier.padding(16.dp))
    }
}

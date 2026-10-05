package com.paulchibamba.margin.feature.settings.screentime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.feature.settings.SettingsSwitch

@Composable
fun DoomAppRow(app: AppScreenTime, onDoomChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    Row(
        modifier.fillMaxWidth().toggleable(app.isDoom, role = Role.Switch, onValueChange = onDoomChange).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(app.label, style = MarginTypography.settingTitle, color = palette.text)
            Text(
                "${app.category.label()} · ${minutesLabel(app.foreground)}",
                style = MarginTypography.label,
                color = palette.faintText,
            )
        }
        SettingsSwitch(isChecked = app.isDoom, isEnabled = true)
    }
}

@Preview(widthDp = 360)
@Composable
private fun DoomAppRowPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        DoomAppRow(DoomAppsPreviewData.apps.first(), onDoomChange = {})
    }
}

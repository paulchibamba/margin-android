package com.paulchibamba.margin.feature.settings

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

@Composable
fun DarkPostsCard(isOn: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    SettingsCard(modifier) {
        Row(
            Modifier.fillMaxWidth().toggleable(isOn, role = Role.Switch, onValueChange = onChange),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Dark posts", style = MarginTypography.settingTitle, color = palette.text)
                Text(
                    "Every post is near-black, Ink or Midnight, with dark cards inside. Easy on the eyes at night.",
                    style = MarginTypography.label,
                    color = palette.faintText,
                )
            }
            SettingsSwitch(isChecked = isOn, isEnabled = true)
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun DarkPostsCardPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        DarkPostsCard(isOn = true, onChange = {}, modifier = Modifier.padding(16.dp))
    }
}

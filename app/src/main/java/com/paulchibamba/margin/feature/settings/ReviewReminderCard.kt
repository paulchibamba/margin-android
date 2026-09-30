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
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun ReviewReminderCard(isOn: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    SettingsCard(modifier) {
        Row(
            Modifier.fillMaxWidth().toggleable(isOn, role = Role.Switch, onValueChange = onChange),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Review reminder", style = MarginTypography.settingTitle, color = palette.text)
                Text(
                    "An evening nudge when reviews are due and today's streak isn't kept yet",
                    style = MarginTypography.label,
                    color = palette.faintText,
                )
            }
            SettingsSwitch(isChecked = isOn, isEnabled = true)
        }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun ReviewReminderCardPreview() {
    ReviewReminderCard(isOn = true, onChange = {}, modifier = Modifier.padding(16.dp))
}

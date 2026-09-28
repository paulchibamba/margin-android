package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun SettingsLinksCard(onOpenReadingOnlyChapters: () -> Unit, onOpenStats: () -> Unit, modifier: Modifier = Modifier) {
    SettingsCard(modifier, contentPadding = PaddingValues(0.dp)) {
        SettingsLink("Reading-only chapters", onOpenReadingOnlyChapters)
        SettingsDivider(Modifier.padding(horizontal = 14.dp))
        SettingsLink("Stats", onOpenStats)
    }
}

@Composable
private fun SettingsLink(label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MarginTypography.settingTitle, color = MarginColors.InkText, modifier = Modifier.weight(1f))
        Icon(painterResource(MarginIcons.ChevronRight), null, Modifier.size(22.dp), MarginColors.PaperTextAhead)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun SettingsLinksCardPreview() {
    SettingsLinksCard(onOpenReadingOnlyChapters = {}, onOpenStats = {}, modifier = Modifier.padding(16.dp))
}

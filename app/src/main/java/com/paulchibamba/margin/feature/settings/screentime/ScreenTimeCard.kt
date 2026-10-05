package com.paulchibamba.margin.feature.settings.screentime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.feature.settings.SettingsCard
import com.paulchibamba.margin.feature.settings.SettingsDivider
import com.paulchibamba.margin.feature.settings.SettingsSwitch

@Composable
fun ScreenTimeCard(
    state: ScreenTimeSettingState,
    onUseScreenTimeClick: () -> Unit,
    onOpenDoomApps: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsCard(modifier, contentPadding = PaddingValues(0.dp)) {
        UseScreenTimeRow(state.hasAccess, onUseScreenTimeClick)
        SettingsDivider(Modifier.padding(horizontal = 14.dp))
        DoomAppsLink(onOpenDoomApps)
        SettingsDivider(Modifier.padding(horizontal = 14.dp))
        DeleteRow(state.isDeleted, onDeleteClick)
    }
}

@Composable
private fun UseScreenTimeRow(hasAccess: Boolean, onClick: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier.fillMaxWidth().toggleable(hasAccess, role = Role.Switch, onValueChange = { onClick() }).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Use screen time", style = MarginTypography.settingTitle, color = palette.text)
            Text(useScreenTimeDetail(hasAccess), style = MarginTypography.label, color = palette.faintText)
        }
        SettingsSwitch(isChecked = hasAccess, isEnabled = true)
    }
}

@Composable
private fun DoomAppsLink(onClick: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Doom apps", style = MarginTypography.settingTitle, color = palette.text)
            Text(DOOM_APPS_DETAIL, style = MarginTypography.label, color = palette.faintText)
        }
        Icon(painterResource(MarginIcons.ChevronRight), null, Modifier.size(22.dp), palette.aheadText)
    }
}

@Composable
private fun DeleteRow(isDeleted: Boolean, onClick: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Text(
        if (isDeleted) "Screen-time data deleted" else "Delete screen-time data",
        style = MarginTypography.settingTitle,
        color = if (isDeleted) palette.faintText else palette.text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isDeleted, role = Role.Button, onClick = onClick)
            .padding(14.dp),
    )
}

private fun useScreenTimeDetail(hasAccess: Boolean): String =
    if (hasAccess) USE_SCREEN_TIME_ON_DETAIL else USE_SCREEN_TIME_OFF_DETAIL

private const val USE_SCREEN_TIME_OFF_DETAIL = "Put doom-scrolling minutes next to Margin minutes in Stats"
private const val USE_SCREEN_TIME_ON_DETAIL = "Margin copies each app's daily minutes from Usage access"
private const val DOOM_APPS_DETAIL = "Social, video and games count as doom unless you say otherwise"

@Preview(widthDp = 360)
@Composable
private fun ScreenTimeCardPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        ScreenTimeCard(
            ScreenTimeSettingState(hasAccess = true),
            onUseScreenTimeClick = {},
            onOpenDoomApps = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

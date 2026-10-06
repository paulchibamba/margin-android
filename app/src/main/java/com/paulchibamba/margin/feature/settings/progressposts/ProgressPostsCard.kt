package com.paulchibamba.margin.feature.settings.progressposts

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
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.feature.settings.SettingsCard
import com.paulchibamba.margin.feature.settings.SettingsDivider
import com.paulchibamba.margin.feature.settings.SettingsSegments
import com.paulchibamba.margin.feature.settings.SettingsSwitch

@Composable
fun ProgressPostsCard(
    state: ProgressPostsUiState,
    onKeyClick: () -> Unit,
    onTestConnection: () -> Unit,
    onModelChange: (LlmModel) -> Unit,
    onDailyCapChange: (MicroDollars) -> Unit,
    onSendExcerptsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsCard(modifier, contentPadding = PaddingValues(0.dp)) {
        KeyRow(state.maskedKey, onKeyClick)
        CardDivider()
        TestConnectionRow(state.connection, state.canTestConnection, onTestConnection)
        CardDivider()
        ModelChoice(state.settings.bakeModel, onModelChange)
        CardDivider()
        DailyCapChoice(state.settings.dailyCap, state.spentToday, onDailyCapChange)
        CardDivider()
        SendExcerptsRow(state.settings.isSendingExcerpts, onSendExcerptsChange)
    }
}

@Composable
private fun KeyRow(maskedKey: String?, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TitleAndDetail("OpenAI API key", keyDetail(maskedKey), Modifier.weight(1f))
        val palette = LocalSurfacePalette.current
        Icon(painterResource(MarginIcons.ChevronRight), null, Modifier.size(22.dp), palette.aheadText)
    }
}

@Composable
private fun TestConnectionRow(check: ConnectionCheck, isEnabled: Boolean, onClick: () -> Unit) {
    TitleAndDetail(
        "Test connection",
        connectionLabel(check),
        Modifier.fillMaxWidth().clickable(enabled = isEnabled, role = Role.Button, onClick = onClick).padding(14.dp),
        isEnabled = isEnabled,
    )
}

@Composable
private fun ModelChoice(selected: LlmModel, onSelect: (LlmModel) -> Unit) {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TitleAndDetail("Model", "Writes progress posts: ${selected.id}")
        SettingsSegments(LlmModel.BAKE_CHOICES, selected, ::modelLabel, onSelect)
    }
}

@Composable
private fun DailyCapChoice(selected: MicroDollars, spentToday: LlmSpend, onSelect: (MicroDollars) -> Unit) {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TitleAndDetail("Daily cap", spendLabel(spentToday))
        SettingsSegments(LlmSettings.DAILY_CAP_CHOICES, selected, ::dollarsLabel, onSelect)
    }
}

@Composable
private fun SendExcerptsRow(isOn: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(isOn, role = Role.Switch, onValueChange = onChange).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TitleAndDetail("Send short excerpts (quotes and re-explains)", SEND_EXCERPTS_DETAIL, Modifier.weight(1f))
        SettingsSwitch(isChecked = isOn, isEnabled = true)
    }
}

@Composable
private fun TitleAndDetail(title: String, detail: String, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    val palette = LocalSurfacePalette.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MarginTypography.settingTitle, color = if (isEnabled) palette.text else palette.faintText)
        Text(detail, style = MarginTypography.label, color = palette.faintText)
    }
}

@Composable
private fun CardDivider() {
    SettingsDivider(Modifier.padding(horizontal = 14.dp))
}

private const val SEND_EXCERPTS_DETAIL = "Up to 3 sentences of a note you've read. Never whole notes or chapters"

@Preview(widthDp = 360)
@Composable
private fun ProgressPostsCardPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        ProgressPostsCard(
            ProgressPostsUiState(maskedKey = "••••WXYZ", spentToday = LlmSpend(1, MicroDollars(340))),
            onKeyClick = {},
            onTestConnection = {},
            onModelChange = {},
            onDailyCapChange = {},
            onSendExcerptsChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(widthDp = 360)
@Composable
private fun TemplatesOnlyPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        ProgressPostsCard(
            ProgressPostsUiState(),
            onKeyClick = {},
            onTestConnection = {},
            onModelChange = {},
            onDailyCapChange = {},
            onSendExcerptsChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

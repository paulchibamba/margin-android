package com.paulchibamba.margin.feature.settings.screentime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.feature.settings.SettingsCard
import com.paulchibamba.margin.feature.settings.SettingsDivider

@Composable
fun DoomAppsScreen(
    state: DoomAppsUiState,
    onBack: () -> Unit,
    onDoomChange: (PackageName, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalSurfacePalette.current
    MarginTheme(palette.skin) {
        StatusBarFollowsSkin()
        Column(modifier.fillMaxSize().background(palette.background).statusBarsPadding()) {
            BackBar(onBack)
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Explanation(if (state.apps.isEmpty() && !state.isLoading) NO_APPS else EXPLANATION)
                if (state.apps.isNotEmpty()) AppList(state.apps, onDoomChange)
            }
        }
    }
}

@Composable
private fun BackBar(onBack: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(painterResource(MarginIcons.ArrowBack), "Back", Modifier.size(24.dp), palette.text)
        }
        Text(
            "Doom apps",
            style = MarginTypography.barTitle,
            color = palette.text,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
private fun Explanation(text: String) {
    Text(
        text,
        style = MarginTypography.detail,
        color = LocalSurfacePalette.current.mutedText,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun AppList(apps: List<AppScreenTime>, onDoomChange: (PackageName, Boolean) -> Unit) {
    SettingsCard(contentPadding = PaddingValues(0.dp)) {
        apps.forEachIndexed { index, app ->
            if (index > 0) SettingsDivider(Modifier.padding(horizontal = 14.dp))
            DoomAppRow(app, onDoomChange = { isDoom -> onDoomChange(app.packageName, isDoom) })
        }
    }
}

private const val EXPLANATION =
    "Apps used in the last 7 days, most used first. Time in doom apps sits next to Margin time in Stats."
private const val NO_APPS = "No app time yet. Turn on Use screen time, then come back tomorrow."

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun DoomAppsScreenPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        DoomAppsScreen(
            DoomAppsUiState(DoomAppsPreviewData.apps, isLoading = false),
            onBack = {},
            onDoomChange = { _, _ -> },
        )
    }
}

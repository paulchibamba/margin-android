package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.domain.memory.DesiredRetention
import kotlin.math.roundToInt

private const val STEPS_PER_UNIT = 100

@Composable
fun RetentionCard(
    retention: Double,
    onChange: (Double) -> Unit,
    onChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalSurfacePalette.current
    SettingsCard(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Desired retention", style = MarginTypography.settingTitle, color = palette.text)
            Text(retentionLabel(retention), style = MarginTypography.monoValue, color = palette.text)
        }
        RetentionSlider(retention, onChange, onChangeFinished)
        RangeLabels()
        WorkloadWarning()
    }
}

@Composable
private fun RetentionSlider(retention: Double, onChange: (Double) -> Unit, onChangeFinished: () -> Unit) {
    val range = DesiredRetention.RANGE
    Slider(
        value = retention.toFloat(),
        onValueChange = { value -> onChange(value.toDouble()) },
        onValueChangeFinished = onChangeFinished,
        valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
        steps = ((range.endInclusive - range.start) * STEPS_PER_UNIT).roundToInt() - 1,
        colors = sliderColors(),
        modifier = Modifier.semantics { contentDescription = "Desired retention" },
    )
}

@Composable
private fun sliderColors(palette: SurfacePalette = LocalSurfacePalette.current) = SliderDefaults.colors(
    thumbColor = palette.accent,
    activeTrackColor = palette.accent,
    inactiveTrackColor = palette.text.copy(alpha = 0.12f),
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
)

@Composable
private fun RangeLabels() {
    val faintText = LocalSurfacePalette.current.faintText
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf(DesiredRetention.RANGE.start, DesiredRetention.RANGE.endInclusive).forEach { bound ->
            Text(retentionLabel(bound), style = MarginTypography.monoSmall, color = faintText)
        }
    }
}

@Composable
private fun WorkloadWarning() {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier.fillMaxWidth().background(palette.warningSurface, RoundedCornerShape(12.dp)).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(painterResource(MarginIcons.Warning), null, Modifier.size(18.dp), palette.warningIcon)
        Text(
            workloadWarning(DesiredRetention.STEEP_WORKLOAD_ABOVE),
            style = MarginTypography.detail,
            color = palette.warningText,
        )
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun RetentionCardPreview() {
    RetentionCard(retention = 0.9, onChange = {}, onChangeFinished = {}, modifier = Modifier.padding(16.dp))
}

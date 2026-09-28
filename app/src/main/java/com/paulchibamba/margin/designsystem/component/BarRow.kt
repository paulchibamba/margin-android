package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val STACKED_FONT_SCALE = 1.3f

@Composable
fun BarRow(labelWidth: Dp, label: @Composable (Modifier) -> Unit, bar: @Composable RowScope.() -> Unit) {
    if (LocalDensity.current.fontScale > STACKED_FONT_SCALE) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            label(Modifier)
            BarLine(Modifier.fillMaxWidth(), bar)
        }
    } else {
        BarLine {
            label(Modifier.width(labelWidth))
            bar()
        }
    }
}

@Composable
private fun BarLine(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

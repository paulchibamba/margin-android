package com.paulchibamba.margin.designsystem.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins

@Composable
fun SkinSwatchCatalog(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        (Skins.all + Skins.Midnight).forEach { SkinSwatch(it) }
    }
}

@Composable
private fun SkinSwatch(skin: Skin) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(skin.background, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(skin.name, style = MarginTypography.postTitle, color = skin.content)
        Text("Muted text for details and captions.", style = MarginTypography.bodySmall, color = skin.mutedContent)
        SkinSignals(skin)
    }
}

@Composable
private fun SkinSignals(skin: Skin) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("Surface", style = MarginTypography.pill, color = skin.content,
            modifier = Modifier.background(skin.surface, RoundedCornerShape(7.dp)).padding(9.dp, 4.dp))
        SignalDot(skin.correct)
        Text("Correct", style = MarginTypography.meta, color = skin.content)
        SignalDot(skin.wrong)
        Text("Wrong", style = MarginTypography.meta, color = skin.content)
    }
}

@Composable
private fun SignalDot(color: Color) {
    Spacer(Modifier.size(12.dp).background(color, CircleShape))
}

@Preview(widthDp = 360)
@Composable
private fun SkinSwatchCatalogPreview() {
    SkinSwatchCatalog(Modifier.padding(16.dp))
}

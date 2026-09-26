package com.paulchibamba.margin.designsystem.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.BrandMark
import com.paulchibamba.margin.designsystem.BrandTile
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun DesignSystemCatalog(modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    LazyColumn(
        modifier.fillMaxSize().background(skin.background).safeDrawingPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        section("Brand") { BrandMarkRow() }
        section("Type") { TypeScaleCatalog() }
        section("Skins") { SkinSwatchCatalog() }
        section("Icons") { IconCatalog() }
    }
}

private fun LazyListScope.section(title: String, content: @Composable () -> Unit) {
    item { Text(title.uppercase(), style = MarginTypography.mono, color = LocalSkin.current.mutedContent) }
    item { content() }
}

@Composable
private fun BrandMarkRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        BrandTile.entries.forEach { BrandMark(size = 96.dp, tile = it) }
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun DesignSystemCatalogPreview() {
    MarginTheme { DesignSystemCatalog() }
}

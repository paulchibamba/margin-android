package com.paulchibamba.margin.designsystem.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme

@Composable
fun IconCatalog(modifier: Modifier = Modifier) {
    val tint = LocalSkin.current.content
    val spacing = Arrangement.spacedBy(14.dp)
    FlowRow(modifier, horizontalArrangement = spacing, verticalArrangement = spacing) {
        MarginIcons.all.forEach { icon ->
            Icon(painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun IconCatalogPreview() {
    MarginTheme { IconCatalog(Modifier.padding(16.dp)) }
}

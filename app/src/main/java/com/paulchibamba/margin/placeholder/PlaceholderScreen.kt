package com.paulchibamba.margin.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin

private const val ROW_COUNT = 60

@Composable
fun PlaceholderScreen(
    title: String,
    skin: Skin,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    links: List<PlaceholderLink> = emptyList(),
) {
    MarginTheme(skin) {
        StatusBarFollowsSkin()
        LazyColumn(
            modifier.fillMaxSize().background(skin.background).statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { PlaceholderHeader(title, onBack) }
            links.forEach { link -> item { PlaceholderLinkRow(link) } }
            placeholderRows()
        }
    }
}

@Composable
private fun PlaceholderHeader(title: String, onBack: (() -> Unit)?) {
    val skin = LocalSkin.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(painterResource(MarginIcons.ArrowBack), contentDescription = "Back", tint = skin.content)
            }
        }
        Text(title, style = MarginTypography.screenTitle, color = skin.content)
    }
}

@Composable
private fun PlaceholderLinkRow(link: PlaceholderLink) {
    val skin = LocalSkin.current
    Row(
        Modifier.fillMaxWidth().background(skin.surface, CircleShape).clickable(onClick = link.onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(link.label, style = MarginTypography.button, color = skin.content, modifier = Modifier.weight(1f))
        Icon(painterResource(MarginIcons.ArrowForward), null, tint = skin.content, modifier = Modifier.size(20.dp))
    }
}

private fun LazyListScope.placeholderRows() {
    items(ROW_COUNT) { index ->
        val skin = LocalSkin.current
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text("Row ${index + 1}", style = MarginTypography.body, color = skin.mutedContent)
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun PlaceholderScreenPreview() {
    PlaceholderScreen(
        title = "Read",
        skin = Skins.Paper,
        links = listOf(PlaceholderLink("Open a book") {}),
    )
}

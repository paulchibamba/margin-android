package com.paulchibamba.margin.designsystem.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography

private val typeScale: List<Pair<String, TextStyle>> = listOf(
    "88" to MarginTypography.display,
    "Never trust the client." to MarginTypography.postHeadline,
    "Bind it. Don't build it." to MarginTypography.postTitle,
    "You're all caught up" to MarginTypography.screenTitle,
    "Which control stops stored XSS?" to MarginTypography.quizQuestion,
    "Server-side validation" to MarginTypography.conceptTitle,
    "margin" to MarginTypography.wordmark,
    "Client-side checks are for UX, not security." to MarginTypography.body,
    "Parameters keep data as data." to MarginTypography.bodySmall,
    "Validate again on the server." to MarginTypography.callout,
    "Read on" to MarginTypography.button,
    "Alice & Bob · Ch 3" to MarginTypography.caption,
    "12/48 introduced" to MarginTypography.meta,
    "Tip" to MarginTypography.pill,
    "Got it" to MarginTypography.railLabel,
    "val q = db.query(sql, id)" to MarginTypography.code,
    "%3C" to MarginTypography.monoLarge,
    "The book's own words, set in Newsreader." to MarginTypography.bookText,
)

@Composable
fun TypeScaleCatalog(modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        typeScale.forEach { (sample, style) -> Text(sample, style = style, color = skin.content) }
    }
}

@Preview(widthDp = 360)
@Composable
private fun TypeScaleCatalogPreview() {
    MarginTheme {
        TypeScaleCatalog(Modifier.background(LocalSkin.current.background).padding(16.dp))
    }
}

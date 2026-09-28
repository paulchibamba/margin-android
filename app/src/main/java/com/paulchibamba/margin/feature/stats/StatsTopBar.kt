package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun StatsTopBar(isCopied: Boolean, onBack: () -> Unit, onCopy: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().height(52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(painterResource(MarginIcons.ArrowBack), "Back", Modifier.size(24.dp), MarginColors.White)
            }
            Text(
                "Stats",
                style = MarginTypography.barTitle,
                color = MarginColors.White,
                modifier = Modifier.semantics { heading() },
            )
        }
        CopyButton(isCopied, onCopy)
    }
}

@Composable
private fun CopyButton(isCopied: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(MarginColors.White.copy(alpha = 0.1f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = if (isCopied) MarginIcons.Check else MarginIcons.ContentCopy
        Icon(painterResource(icon), null, Modifier.size(17.dp), MarginColors.White)
        val label = if (isCopied) "Copied" else "Copy as text"
        Text(label, style = MarginTypography.smallButton, color = MarginColors.White)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun StatsTopBarPreview() {
    StatsTopBar(isCopied = false, onBack = {}, onCopy = {})
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun StatsTopBarCopiedPreview() {
    StatsTopBar(isCopied = true, onBack = {}, onCopy = {})
}

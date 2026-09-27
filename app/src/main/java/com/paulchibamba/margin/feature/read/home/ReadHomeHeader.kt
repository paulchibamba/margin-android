package com.paulchibamba.margin.feature.read.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.BrandMark
import com.paulchibamba.margin.designsystem.BrandTile
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins

@Composable
fun ReadHomeHeader(streak: Int, onOpenSettings: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(size = 30.dp, tile = BrandTile.Dark)
            Text(
                "Read",
                style = MarginTypography.homeTitle,
                color = MarginColors.InkText,
                modifier = Modifier.semantics { heading() },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StreakChip(streak)
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(40.dp)) {
                Icon(painterResource(MarginIcons.Settings), "Settings", Modifier.size(24.dp), MarginColors.InkText)
            }
        }
    }
}

@Composable
private fun StreakChip(streak: Int) {
    Row(
        Modifier
            .background(Skins.Paper.surface, CircleShape)
            .padding(PaddingValues(start = 8.dp, top = 6.dp, end = 11.dp, bottom = 6.dp))
            .semantics(mergeDescendants = true) { contentDescription = "$streak day streak" },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.LocalFireDepartment), null, Modifier.size(19.dp), MarginColors.Streak)
        Text("$streak", style = MarginTypography.streakChip, color = MarginColors.InkText)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun ReadHomeHeaderPreview() {
    MarginTheme(Skins.Paper) { ReadHomeHeader(streak = 7, onOpenSettings = {}, modifier = Modifier.padding(18.dp)) }
}

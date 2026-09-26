package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography

private const val INACTIVE_ALPHA = 0.6f

@Composable
fun MarginNavigationBar(
    selectedTab: NavigationTab,
    onTabSelect: (NavigationTab) -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = NavigationBarColors.of(isLight)
    Column(modifier.fillMaxWidth().background(colors.background).navigationBarsPadding()) {
        HorizontalDivider(thickness = 1.dp, color = colors.divider)
        Row(
            Modifier.fillMaxWidth().height(60.dp).selectableGroup(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavigationTab.entries.forEach { tab ->
                NavigationTabItem(tab, isSelected = tab == selectedTab, colors, onClick = { onTabSelect(tab) })
            }
        }
    }
}

@Composable
private fun NavigationTabItem(
    tab: NavigationTab,
    isSelected: Boolean,
    colors: NavigationBarColors,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .width(96.dp)
            .selectable(selected = isSelected, onClick = onClick, role = Role.Tab)
            .alpha(if (isSelected) 1f else INACTIVE_ALPHA),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TabPill(tab, isSelected, colors)
        Spacer(Modifier.height(4.dp))
        val style = if (isSelected) MarginTypography.navLabelActive else MarginTypography.navLabel
        Text(tab.label, style = style, color = colors.content)
    }
}

@Composable
private fun TabPill(tab: NavigationTab, isSelected: Boolean, colors: NavigationBarColors) {
    val fill = if (isSelected) colors.activePill else Color.Transparent
    Box(Modifier.size(60.dp, 30.dp).background(fill, CircleShape), contentAlignment = Alignment.Center) {
        val icon = if (isSelected) tab.activeIcon else tab.icon
        Icon(painterResource(icon), contentDescription = null, tint = colors.content, modifier = Modifier.size(24.dp))
    }
}

private class NavigationBarColors(
    val background: Color,
    val content: Color,
    val divider: Color,
    val activePill: Color,
) {
    companion object {
        private val Light = NavigationBarColors(
            background = MarginColors.Paper,
            content = MarginColors.InkText,
            divider = MarginColors.InkText.copy(alpha = 0.08f),
            activePill = MarginColors.InkText.copy(alpha = 0.1f),
        )
        private val Dark = NavigationBarColors(
            background = MarginColors.Ink,
            content = MarginColors.White,
            divider = MarginColors.White.copy(alpha = 0.08f),
            activePill = MarginColors.White.copy(alpha = 0.14f),
        )

        fun of(isLight: Boolean) = if (isLight) Light else Dark
    }
}

@Preview(widthDp = 360)
@Composable
private fun MarginNavigationBarDarkPreview() {
    MarginNavigationBar(selectedTab = NavigationTab.Feed, onTabSelect = {}, isLight = false)
}

@Preview(widthDp = 360)
@Composable
private fun MarginNavigationBarLightPreview() {
    MarginNavigationBar(selectedTab = NavigationTab.Read, onTabSelect = {}, isLight = true)
}

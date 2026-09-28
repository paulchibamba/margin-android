package com.paulchibamba.margin.feature.celebration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun CelebrationButton(label: String, container: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(container)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MarginTypography.button, color = MarginColors.InkText)
    }
}

@Composable
fun ShareButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(1.5.dp, MarginColors.White.copy(alpha = 0.25f), CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.IosShare), null, Modifier.size(18.dp), MarginColors.White)
        Text("Share", style = MarginTypography.readerButton, color = MarginColors.White)
    }
}

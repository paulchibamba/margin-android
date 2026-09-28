package com.paulchibamba.margin.feature.celebration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.domain.rewards.BadgeKind

private val DIAMOND_SIZE = 132.dp
private val DIAMOND_EXTENT = 187.dp
private val DIAMOND_CORNER = 38.dp
private const val DIAMOND_TURN = 45f
private const val DIAMOND_START_SCALE = 0.5f

private const val GRADIENT_START_X = 0.33f
private const val GRADIENT_END_X = 0.67f

@Composable
fun BadgeDiamond(kind: BadgeKind, modifier: Modifier = Modifier) {
    val scale by rememberPopIn(from = DIAMOND_START_SCALE)
    Box(
        modifier
            .size(DIAMOND_EXTENT)
            .celebrationGlow(MarginColors.Lime.copy(alpha = 0.28f), radius = 118.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(DIAMOND_SIZE)
                .graphicsLayer { rotationZ = DIAMOND_TURN }
                .diamondFill(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(kind.icon()),
                contentDescription = null,
                tint = MarginColors.InkText,
                modifier = Modifier.size(60.dp).graphicsLayer { rotationZ = -DIAMOND_TURN },
            )
        }
    }
}

private fun Modifier.diamondFill(): Modifier = drawBehind {
    val gradient = Brush.linearGradient(
        listOf(MarginColors.BadgeLimeLight, MarginColors.BadgeLimeDeep),
        start = Offset(size.width * GRADIENT_START_X, 0f),
        end = Offset(size.width * GRADIENT_END_X, size.height),
    )
    drawRoundRect(gradient, cornerRadius = CornerRadius(DIAMOND_CORNER.toPx()))
}

@Preview(backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun BadgeDiamondIntroducedPreview() {
    BadgeDiamond(BadgeKind.INTRODUCED, Modifier.padding(24.dp))
}

@Preview(backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun BadgeDiamondRememberedPreview() {
    BadgeDiamond(BadgeKind.REMEMBERED, Modifier.padding(24.dp))
}

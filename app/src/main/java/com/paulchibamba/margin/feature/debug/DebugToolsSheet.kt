package com.paulchibamba.margin.feature.debug

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors

private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private const val SCRIM_ALPHA = 0.55f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugToolsSheet(onDismiss: () -> Unit, content: @Composable (Modifier) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = SheetShape,
        containerColor = MarginColors.InkSheet,
        contentColor = MarginColors.White,
        scrimColor = Color.Black.copy(alpha = SCRIM_ALPHA),
    ) {
        content(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 30.dp).navigationBarsPadding())
    }
}

package com.paulchibamba.margin.feature.read.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val HandleShape = RoundedCornerShape(4.dp)
private const val SCRIM_ALPHA = 0.4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCoverSheet(hasCover: Boolean, choices: CoverChoices, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = SheetShape,
        containerColor = LocalSurfacePalette.current.card,
        contentColor = LocalSurfacePalette.current.text,
        scrimColor = Color.Black.copy(alpha = SCRIM_ALPHA),
        dragHandle = { DragHandle() },
    ) {
        CoverActions(hasCover, choices)
    }
}

@Composable
private fun CoverActions(hasCover: Boolean, choices: CoverChoices) {
    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp).navigationBarsPadding()) {
        Text(
            "Book cover",
            style = MarginTypography.sheetTitle,
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp).semantics { heading() },
        )
        CoverAction(MarginIcons.PhotoLibrary, "Choose from gallery", choices.onChooseFromGallery)
        CoverAction(MarginIcons.ImageSearch, "Search the web", choices.onSearchWeb)
        if (hasCover) CoverAction(MarginIcons.Delete, "Remove cover", choices.onRemove)
    }
}

@Composable
private fun CoverAction(icon: Int, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val palette = LocalSurfacePalette.current
        Icon(painterResource(icon), contentDescription = null, Modifier.size(24.dp), palette.text)
        Text(label, style = MarginTypography.settingTitle, color = palette.text)
    }
}

@Composable
private fun DragHandle() {
    Box(
        Modifier.padding(top = 10.dp, bottom = 6.dp).size(width = 36.dp, height = 4.dp)
            .background(LocalSurfacePalette.current.text.copy(alpha = 0.2f), HandleShape),
    )
}

@Preview(widthDp = 360, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun CoverActionsPreview() {
    CoverActions(hasCover = true, CoverChoices(onChooseFromGallery = {}, onSearchWeb = {}, onRemove = {}))
}

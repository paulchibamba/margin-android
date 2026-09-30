package com.paulchibamba.margin.feature.read.cover

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun UseCoverDialog(bookTitle: String, isSaving: Boolean, onUse: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = { if (!isSaving) onCancel() },
        confirmButton = { DialogButton(if (isSaving) "Saving…" else "Use", isEnabled = !isSaving, onUse) },
        dismissButton = { DialogButton("Cancel", isEnabled = !isSaving, onCancel) },
        title = { Text("Use as cover for $bookTitle?", style = MarginTypography.sheetTitle) },
        text = { Text(COPY_NOTE, style = MarginTypography.bodySmall) },
        containerColor = MarginColors.PaperCard,
        titleContentColor = MarginColors.InkText,
        textContentColor = MarginColors.PaperTextMuted,
    )
}

@Composable
private fun DialogButton(label: String, isEnabled: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = isEnabled) {
        Text(label, style = MarginTypography.button, color = MarginColors.InkText)
    }
}

private const val COPY_NOTE = "Margin keeps a small private copy. It won't appear in your gallery or downloads."

@Preview
@Composable
private fun UseCoverDialogPreview() {
    UseCoverDialog("Alice and Bob Learn Application Security", isSaving = false, onUse = {}, onCancel = {})
}

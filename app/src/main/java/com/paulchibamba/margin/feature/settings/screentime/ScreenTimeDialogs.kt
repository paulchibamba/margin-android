package com.paulchibamba.margin.feature.settings.screentime

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun ScreenTimeDialogs(
    dialog: ScreenTimeDialog?,
    onOpenUsageAccess: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit,
) {
    when (dialog) {
        ScreenTimeDialog.TURN_ON -> ConfirmDialog(TurnOn, onOpenUsageAccess, onDismiss)
        ScreenTimeDialog.TURN_OFF -> ConfirmDialog(TurnOff, onOpenUsageAccess, onDismiss)
        ScreenTimeDialog.CONFIRM_DELETE -> ConfirmDialog(Delete, onDeleteConfirmed, onDismiss)
        null -> Unit
    }
}

@Composable
private fun ConfirmDialog(copy: DialogCopy, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val palette = LocalSurfacePalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { DialogButton(copy.confirm, onConfirm) },
        dismissButton = { DialogButton("Cancel", onDismiss) },
        title = { Text(copy.title, style = MarginTypography.sheetTitle) },
        text = { Text(copy.text, style = MarginTypography.bodySmall) },
        containerColor = palette.card,
        titleContentColor = palette.text,
        textContentColor = palette.mutedText,
    )
}

@Composable
private fun DialogButton(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label, style = MarginTypography.button, color = LocalSurfacePalette.current.text)
    }
}

private class DialogCopy(val title: String, val text: String, val confirm: String)

private const val OPEN_USAGE_ACCESS = "Open Usage access"

private val TurnOn = DialogCopy(
    title = "Use screen time?",
    text = "Margin copies how many minutes you spent in each app every day, so Stats can put doom-scrolling next " +
        "to learning. It sees daily totals per app, never what you do inside them, and never sends them " +
        "anywhere. On the next screen, find Margin and allow usage access.",
    confirm = OPEN_USAGE_ACCESS,
)

private val TurnOff = DialogCopy(
    title = "Stop using screen time?",
    text = "On the next screen, turn off usage access for Margin. Margin stops copying screen time. What it has " +
        "already saved stays until you delete it.",
    confirm = OPEN_USAGE_ACCESS,
)

private val Delete = DialogCopy(
    title = "Delete screen-time data?",
    text = "This removes every app's saved minutes from Margin and from Stats. Your doom-app choices stay. While " +
        "usage access is on, Margin starts again from today.",
    confirm = "Delete",
)

@Preview
@Composable
private fun TurnOnDialogPreview() {
    ScreenTimeDialogs(ScreenTimeDialog.TURN_ON, onOpenUsageAccess = {}, onDeleteConfirmed = {}, onDismiss = {})
}

@Preview
@Composable
private fun DeleteDialogPreview() {
    ScreenTimeDialogs(ScreenTimeDialog.CONFIRM_DELETE, onOpenUsageAccess = {}, onDeleteConfirmed = {}, onDismiss = {})
}

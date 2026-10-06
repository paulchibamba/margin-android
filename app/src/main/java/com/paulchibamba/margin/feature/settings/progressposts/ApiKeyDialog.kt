package com.paulchibamba.margin.feature.settings.progressposts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.SurfacePalette

@Composable
fun ApiKeyDialog(
    state: KeyDialogState,
    hasKey: Boolean,
    onSave: (String) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (state == KeyDialogState.CLOSED) return
    var pasted by remember { mutableStateOf("") }
    val palette = LocalSurfacePalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { DialogButton("Save", onClick = { onSave(pasted) }) },
        dismissButton = { DismissButtons(hasKey, onRemove, onDismiss) },
        title = { Text("OpenAI API key", style = MarginTypography.sheetTitle) },
        text = { KeyField(pasted, isNotAKey = state == KeyDialogState.NOT_A_KEY, onChange = { pasted = it }) },
        containerColor = palette.card,
        titleContentColor = palette.text,
        textContentColor = palette.mutedText,
    )
}

@Composable
private fun KeyField(pasted: String, isNotAKey: Boolean, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(KEY_EXPLANATION, style = MarginTypography.bodySmall)
        OutlinedTextField(
            value = pasted,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Paste the key") },
            isError = isNotAKey,
            supportingText = if (isNotAKey) ({ Text("That doesn't look like a key") }) else null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            colors = keyFieldColors(LocalSurfacePalette.current),
        )
    }
}

@Composable
private fun DismissButtons(hasKey: Boolean, onRemove: () -> Unit, onDismiss: () -> Unit) {
    Row {
        if (hasKey) DialogButton("Remove key", onRemove)
        DialogButton("Cancel", onDismiss)
    }
}

@Composable
private fun DialogButton(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label, style = MarginTypography.button, color = LocalSurfacePalette.current.text)
    }
}

@Composable
private fun keyFieldColors(palette: SurfacePalette) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = palette.text,
    unfocusedTextColor = palette.text,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = palette.accent,
    focusedBorderColor = palette.accent,
    unfocusedBorderColor = palette.divider,
    focusedLabelColor = palette.mutedText,
    unfocusedLabelColor = palette.faintText,
)

private const val KEY_EXPLANATION = "Use a project key with a low spend limit. Margin keeps it encrypted on this " +
    "phone and only sends it to OpenAI. Without a key, progress posts use templates."

@Preview
@Composable
private fun ApiKeyDialogPreview() {
    ApiKeyDialog(KeyDialogState.OPEN, hasKey = true, onSave = {}, onRemove = {}, onDismiss = {})
}

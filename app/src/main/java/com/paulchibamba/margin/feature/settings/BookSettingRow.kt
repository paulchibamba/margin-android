package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.BookSettings

@Composable
fun BookSettingRow(book: BookSettingState, onChange: (BookSettings) -> Unit, modifier: Modifier = Modifier) {
    val settings = book.settings
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .toggleable(settings.isActive, enabled = book.canToggle, role = Role.Switch) { isActive ->
                    onChange(settings.copy(isActive = isActive))
                },
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BookSettingTitle(book, Modifier.weight(1f))
            SettingsSwitch(isChecked = settings.isActive, isEnabled = book.canToggle)
        }
        if (settings.isActive) {
            PrioritySelector(settings.priority, onSelect = { priority -> onChange(settings.copy(priority = priority)) })
        }
    }
}

@Composable
private fun BookSettingTitle(book: BookSettingState, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(book.title, style = MarginTypography.settingTitle, color = MarginColors.InkText)
        if (!book.settings.isActive) {
            Text("Reviews only", style = MarginTypography.label, color = MarginColors.PaperTextFaint)
        }
    }
}

@Preview(widthDp = 328, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun BookSettingRowPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsPreviewData.settings.books.forEach { book -> BookSettingRow(book, onChange = {}) }
    }
}

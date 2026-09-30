package com.paulchibamba.margin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.BookSettings

@Composable
fun ActiveBooksSection(state: SettingsUiState, onChange: (BookSettings) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionTitle("Active books")
            Text(
                activeBooksLabel(state.activeCount, state.maxActive),
                style = MarginTypography.label,
                color = LocalSurfacePalette.current.faintText,
            )
        }
        SettingsCard {
            state.books.forEachIndexed { index, book ->
                if (index > 0) SettingsDivider()
                BookSettingRow(book, onChange)
            }
        }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun ActiveBooksSectionPreview() {
    ActiveBooksSection(SettingsPreviewData.settings, onChange = {}, modifier = Modifier.padding(16.dp))
}

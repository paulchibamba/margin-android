package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.feature.feed.shortIntervalLabel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

private val GradeShape = RoundedCornerShape(14.dp)
private const val UNCHOSEN_ALPHA = 0.4f

private val selfGrades = listOf(Rating.AGAIN to "Missed it", Rating.HARD to "Hard", Rating.GOOD to "Got it")

@Composable
fun SelfGradeButtons(
    intervals: Map<Rating, Duration>,
    chosen: Rating?,
    onGrade: (Rating) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("How did you do?", style = MarginTypography.caption, color = LocalSkin.current.mutedContent)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            selfGrades.forEach { (rating, label) ->
                GradeButton(label, intervals[rating], rating, chosen, onGrade, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GradeButton(
    label: String,
    interval: Duration?,
    rating: Rating,
    chosen: Rating?,
    onGrade: (Rating) -> Unit,
    modifier: Modifier,
) {
    val isPrimary = rating == Rating.GOOD
    val textColor = if (isPrimary) MarginColors.InkText else LocalSkin.current.content
    Column(
        modifier
            .alpha(if (chosen == null || chosen == rating) 1f else UNCHOSEN_ALPHA)
            .heightIn(min = 48.dp)
            .background(backgroundOf(isPrimary, LocalSkin.current), GradeShape)
            .clickable(enabled = chosen == null, role = Role.Button) { onGrade(rating) }
            .semantics { selected = chosen == rating }
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MarginTypography.smallButton, color = textColor)
        interval?.let { Text(shortIntervalLabel(it), style = MarginTypography.monoSmall, color = textColor) }
    }
}

private fun backgroundOf(isPrimary: Boolean, skin: Skin): Color = if (isPrimary) MarginColors.Lime else skin.surface

private val previewIntervals = mapOf(Rating.AGAIN to 10.minutes, Rating.HARD to 2.days, Rating.GOOD to 6.days)

@Composable
private fun SelfGradeButtonsPreviewOn(skin: Skin, chosen: Rating?) {
    MarginTheme(skin) {
        Box(Modifier.background(skin.background).padding(16.dp)) {
            SelfGradeButtons(previewIntervals, chosen, onGrade = {})
        }
    }
}

@Preview(widthDp = 290)
@Composable
private fun SelfGradeButtonsForestPreview() = SelfGradeButtonsPreviewOn(Skins.Forest, chosen = null)

@Preview(widthDp = 290)
@Composable
private fun SelfGradeButtonsPaperPreview() = SelfGradeButtonsPreviewOn(Skins.Paper, chosen = Rating.HARD)

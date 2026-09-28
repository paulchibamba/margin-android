package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.usecase.StatsReport

@Composable
fun ReviewGradesCard(report: StatsReport, modifier: Modifier = Modifier) {
    StatsCard("Reviews by grade", modifier) {
        if (report.reviewCount == 0) {
            EmptyStatsLine("No reviews yet")
        } else {
            GradeBar(report)
            GradeLegend(report)
        }
    }
}

@Composable
private fun GradeBar(report: StatsReport) {
    Row(
        Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        StatsReport.GRADES.filter { report.countOf(it) > 0 }.forEach { grade ->
            Box(Modifier.weight(report.shareOf(grade).toFloat()).fillMaxHeight().background(colorOf(grade)))
        }
    }
}

@Composable
private fun GradeLegend(report: StatsReport) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        StatsReport.GRADES.forEach { grade ->
            Text(
                "${grade.label()} ${percentLabel(report.shareOf(grade))}",
                style = MarginTypography.label,
                color = MarginColors.White.copy(alpha = 0.75f),
            )
        }
    }
}

private fun colorOf(grade: Rating): Color = when (grade) {
    Rating.AGAIN -> MarginColors.Wrong
    Rating.HARD -> MarginColors.Save
    Rating.GOOD, Rating.EASY -> MarginColors.Lime
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ReviewGradesCardPreview() {
    ReviewGradesCard(StatsPreviewData.busy, Modifier.padding(16.dp))
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ReviewGradesCardEmptyPreview() {
    ReviewGradesCard(StatsPreviewData.empty, Modifier.padding(16.dp))
}

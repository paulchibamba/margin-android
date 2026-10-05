package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.usecase.ScreenTimeDay
import com.paulchibamba.margin.feature.settings.screentime.minutesLabel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration

private val BarShape = RoundedCornerShape(4.dp)
private val MarginBar = MarginColors.Lime
private val DoomBar = MarginColors.Wrong

@Composable
fun ScreenTimeByDayCard(days: List<ScreenTimeDay>, modifier: Modifier = Modifier) {
    val longest = days.maxOfOrNull { maxOf(it.margin, it.doom) } ?: Duration.ZERO
    StatsCard("Margin vs doom, by day", modifier) {
        Legend()
        days.forEach { day -> DayRow(day, longest) }
    }
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendItem("Margin, active", MarginBar)
        LegendItem("Doom apps", DoomBar)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Text(label, style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.75f))
    }
}

@Composable
private fun DayRow(day: ScreenTimeDay, longest: Duration) {
    Row(
        Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = descriptionOf(day) },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            dayLabel(day.date),
            style = MarginTypography.footnote,
            color = MarginColors.White,
            modifier = Modifier.width(52.dp),
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DurationBar(day.margin, longest, MarginBar)
            DurationBar(day.doom, longest, DoomBar)
        }
    }
}

@Composable
private fun DurationBar(duration: Duration, longest: Duration, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(8.dp)) {
            Box(Modifier.fillMaxWidth(shareOf(duration, longest)).fillMaxHeight().background(color, BarShape))
        }
        Text(
            minutesLabel(duration),
            style = MarginTypography.monoSmall,
            color = MarginColors.White,
            textAlign = TextAlign.End,
            softWrap = false,
            modifier = Modifier.widthIn(min = 72.dp),
        )
    }
}

private fun shareOf(duration: Duration, longest: Duration): Float =
    if (longest.isPositive()) (duration / longest).toFloat().coerceIn(0f, 1f) else 0f

private fun dayLabel(date: LocalDate): String = DateTimeFormatter.ofPattern("EEE d", Locale.getDefault()).format(date)

private fun descriptionOf(day: ScreenTimeDay): String {
    val weekday = day.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "$weekday ${day.date.dayOfMonth}: Margin ${minutesLabel(day.margin)}, doom apps ${minutesLabel(day.doom)}"
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ScreenTimeByDayCardPreview() {
    ScreenTimeByDayCard(StatsPreviewData.screenTime.days, Modifier.padding(16.dp))
}

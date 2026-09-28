package com.paulchibamba.margin.feature.celebration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.rewards.StreakDay
import com.paulchibamba.margin.domain.rewards.StreakDayStatus
import com.paulchibamba.margin.domain.usecase.StreakSummary

private val DAY_SIZE = 30.dp
private val TODAY_RING = 4.dp

@Composable
fun WeekStrip(streak: StreakSummary, todayFill: Float, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        streak.week.forEach { day ->
            val isToday = day.date == streak.today
            WeekDay(day, isToday, fill = if (isToday) todayFill else 1f)
        }
    }
}

@Composable
private fun WeekDay(day: StreakDay, isToday: Boolean, fill: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            weekDayLetter(day.date),
            style = if (isToday) MarginTypography.weekDayToday else MarginTypography.weekDay,
            color = if (isToday) MarginColors.Streak else MarginColors.White.copy(alpha = 0.6f),
        )
        DayCircle(isDone = day.status == StreakDayStatus.DONE, isToday = isToday, fill = fill)
    }
}

@Composable
private fun DayCircle(isDone: Boolean, isToday: Boolean, fill: Float) {
    Box(
        Modifier
            .size(DAY_SIZE)
            .then(if (isToday && isDone) Modifier.todayRing(fill) else Modifier)
            .background(MarginColors.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (isDone) DoneFill(fill)
    }
}

@Composable
private fun DoneFill(fill: Float) {
    Box(
        Modifier
            .size(DAY_SIZE)
            .graphicsLayer { scaleX = fill; scaleY = fill }
            .background(MarginColors.Streak, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(MarginIcons.Check), null, Modifier.size(18.dp), MarginColors.InkText)
    }
}

private fun Modifier.todayRing(fill: Float): Modifier = drawBehind {
    val ringRadius = size.minDimension / 2 + TODAY_RING.toPx() * fill.coerceIn(0f, 1f)
    drawCircle(MarginColors.Streak.copy(alpha = 0.3f), ringRadius)
}

@Preview(backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun WeekStripPreview() {
    WeekStrip(CelebrationPreviewData.streak, todayFill = 1f, modifier = Modifier.padding(16.dp))
}

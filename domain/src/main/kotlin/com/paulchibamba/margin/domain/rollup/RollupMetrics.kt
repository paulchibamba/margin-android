package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.screentime.AppScreenTime

data class RollupMetrics(
    val time: TimeMetrics,
    val sessions: SessionMetrics,
    val posts: PostMetrics,
    val rereads: RereadMetrics,
    val answers: AnswerMetrics,
    val reading: ReadingMetrics,
    val screenTime: ScreenTimeMetrics? = null,
) {
    fun withScreenTime(apps: List<AppScreenTime>) = copy(screenTime = ScreenTimeRollup.of(apps, time.active))

    companion object {
        fun of(day: DayEvents) = RollupMetrics(
            time = TimeRollup.of(day),
            sessions = SessionRollup.of(day),
            posts = PostRollup.of(day),
            rereads = RereadRollup.of(day),
            answers = AnswerRollup.of(day),
            reading = ReadingRollup.of(day),
        )
    }
}

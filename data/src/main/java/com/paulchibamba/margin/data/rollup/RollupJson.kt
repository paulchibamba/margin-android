package com.paulchibamba.margin.data.rollup

import kotlinx.serialization.Serializable

@Serializable
data class RollupJson(
    val activeMin: Double,
    val idleMin: Double,
    val sessions: Int,
    val medianSessionMin: Double?,
    val entries: Map<String, Int>,
    val postsSeen: Int,
    val postExposures: Int,
    val glances: Int,
    val deepReads: Int,
    val glanceRate: Double?,
    val deepRate: Double?,
    val ratioByFormat: Map<String, FormatRatio>,
    val revisits: Int,
    val notesReopened: Int,
    val scrollBacks: Int,
    val rereadHotspots: List<Hotspot>,
    val answers: Int,
    val correctAnswers: Int,
    val accuracy: Double?,
    val medianMsToAnswer: Long?,
    val exitFormats: Map<String, Int>,
    val notesRead: Int,
    val wpmByBook: Map<String, Pace>,
    val wpmByTimeOfDay: Map<String, Pace>,
    val dropCompleted: Int? = null,
    val notificationOpenRate: Double? = null,
    val screenMin: Double? = null,
    val doomMin: Double? = null,
    val marginShare: Double? = null,
    val topDoomApps: List<String>? = null,
) {
    @Serializable
    data class FormatRatio(val median: Double, val posts: Int)

    @Serializable
    data class Hotspot(val kind: String, val id: String, val count: Int)

    @Serializable
    data class Pace(val wpm: Int?, val words: Int, val activeMs: Long)
}

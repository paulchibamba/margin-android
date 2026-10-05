package com.paulchibamba.margin.data.rollup

import com.paulchibamba.margin.data.repository.mapper.FormatNames
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rollup.AnswerMetrics
import com.paulchibamba.margin.domain.rollup.FormatAttention
import com.paulchibamba.margin.domain.rollup.PostMetrics
import com.paulchibamba.margin.domain.rollup.ReadingMetrics
import com.paulchibamba.margin.domain.rollup.ReadingPace
import com.paulchibamba.margin.domain.rollup.RereadHotspot
import com.paulchibamba.margin.domain.rollup.RereadMetrics
import com.paulchibamba.margin.domain.rollup.RereadSubject
import com.paulchibamba.margin.domain.rollup.RollupMetrics
import com.paulchibamba.margin.domain.rollup.ScreenTimeMetrics
import com.paulchibamba.margin.domain.rollup.SessionMetrics
import com.paulchibamba.margin.domain.rollup.TimeMetrics
import com.paulchibamba.margin.domain.rollup.TimeOfDay
import com.paulchibamba.margin.domain.tracking.SessionEntry
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

object RollupMapper {
    private const val CONCEPT = "concept"
    private const val NOTE = "note"
    private const val HUNDREDTHS = 100.0
    private const val MS_PER_MINUTE = 60_000.0

    fun toJson(metrics: RollupMetrics): RollupJson = with(metrics) {
        RollupJson(
            activeMin = minutesOf(time.active),
            idleMin = minutesOf(time.idle),
            sessions = sessions.sessions,
            medianSessionMin = sessions.medianSession?.let(::minutesOf),
            entries = sessions.entries.mapKeys { (entry, _) -> keyOf(entry) },
            postsSeen = posts.postsSeen,
            postExposures = posts.exposures,
            glances = posts.glances,
            deepReads = posts.deepReads,
            glanceRate = posts.glanceRate,
            deepRate = posts.deepRate,
            ratioByFormat = posts.ratioByFormat.entries.associate { (format, attention) ->
                FormatNames.nameOf(format) to RollupJson.FormatRatio(attention.medianRatio, attention.exposures)
            },
            revisits = rereads.revisits,
            notesReopened = rereads.notesReopened,
            scrollBacks = rereads.scrollBacks,
            rereadHotspots = rereads.hotspots.map(::hotspotJsonOf),
            answers = answers.answers,
            correctAnswers = answers.correct,
            accuracy = answers.accuracy,
            medianMsToAnswer = answers.medianTimeToAnswer?.inWholeMilliseconds,
            exitFormats = sessions.exitFormats.mapKeys { (format, _) -> FormatNames.nameOf(format) },
            notesRead = reading.notesRead,
            wpmByBook = reading.paceByBook.entries.associate { (book, pace) -> book.value to paceJsonOf(pace) },
            wpmByTimeOfDay = reading.paceByTimeOfDay.entries.associate { (time, pace) ->
                keyOf(time) to paceJsonOf(pace)
            },
            screenMin = screenTime?.screen?.let(::minutesOf),
            doomMin = screenTime?.doom?.let(::minutesOf),
            marginShare = screenTime?.marginShare,
            topDoomApps = screenTime?.topDoomApps,
        )
    }

    fun fromJson(json: RollupJson) = RollupMetrics(
        time = TimeMetrics(json.activeMin.minutes, json.idleMin.minutes),
        sessions = SessionMetrics(
            sessions = json.sessions,
            medianSession = json.medianSessionMin?.minutes,
            entries = json.entries.mapKeys { (key, _) -> enumValueOf<SessionEntry>(key.uppercase()) },
            exitFormats = json.exitFormats.mapKeys { (name, _) -> FormatNames.formatOf(name) },
        ),
        posts = PostMetrics(json.postsSeen, json.postExposures, json.glances, json.deepReads, ratiosOf(json)),
        rereads = RereadMetrics(
            revisits = json.revisits,
            notesReopened = json.notesReopened,
            scrollBacks = json.scrollBacks,
            hotspots = json.rereadHotspots.map(::hotspotOf),
        ),
        answers = AnswerMetrics(json.answers, json.correctAnswers, json.medianMsToAnswer?.milliseconds),
        reading = ReadingMetrics(
            notesRead = json.notesRead,
            paceByBook = json.wpmByBook.entries.associate { (book, pace) -> BookSlug(book) to paceOf(pace) },
            paceByTimeOfDay = json.wpmByTimeOfDay.entries.associate { (time, pace) ->
                enumValueOf<TimeOfDay>(time.uppercase()) to paceOf(pace)
            },
        ),
        screenTime = screenTimeOf(json),
    )

    private fun screenTimeOf(json: RollupJson): ScreenTimeMetrics? {
        val screenMin = json.screenMin ?: return null
        return ScreenTimeMetrics(
            screen = screenMin.minutes,
            doom = (json.doomMin ?: 0.0).minutes,
            margin = json.activeMin.minutes,
            topDoomApps = json.topDoomApps.orEmpty(),
        )
    }

    private fun ratiosOf(json: RollupJson): Map<Format, FormatAttention> =
        json.ratioByFormat.entries.associate { (name, ratio) ->
            FormatNames.formatOf(name) to FormatAttention(ratio.median, ratio.posts)
        }

    private fun hotspotJsonOf(hotspot: RereadHotspot): RollupJson.Hotspot = when (val subject = hotspot.subject) {
        is RereadSubject.OfConcept -> RollupJson.Hotspot(CONCEPT, subject.conceptId.value, hotspot.count)
        is RereadSubject.OfNote -> RollupJson.Hotspot(NOTE, subject.noteId.value, hotspot.count)
    }

    private fun hotspotOf(json: RollupJson.Hotspot): RereadHotspot {
        val subject = if (json.kind == CONCEPT) {
            RereadSubject.OfConcept(ConceptId(json.id))
        } else {
            RereadSubject.OfNote(NoteId(json.id))
        }
        return RereadHotspot(subject, json.count)
    }

    private fun paceJsonOf(pace: ReadingPace) =
        RollupJson.Pace(pace.wordsPerMinute, pace.words, pace.activeTime.inWholeMilliseconds)

    private fun paceOf(json: RollupJson.Pace) = ReadingPace(json.words, json.activeMs.milliseconds)

    private fun minutesOf(duration: Duration): Double =
        (duration.inWholeMilliseconds / MS_PER_MINUTE * HUNDREDTHS).roundToLong() / HUNDREDTHS

    private fun keyOf(value: Enum<*>): String = value.name.lowercase()
}

package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.rewards.StreakCalculator
import javax.inject.Inject

class MarkNoteRead @Inject constructor(private val progress: ProgressRepository, private val clock: Clock) {

    suspend operator fun invoke(note: NoteId): RecordedRead {
        val now = clock.now()
        val isNewlyRead = progress.markNoteRead(note, now)
        if (!isNewlyRead) return RecordedRead(isNewlyRead = false, isStreakExtended = false)
        val streak = StreakCalculator(clock.zone())
        val activity = progress.addActivity(streak.today(now), postsSeen = 0, notesRead = 1)
        return RecordedRead(isNewlyRead = true, isStreakExtended = streak.isExtendedBy(activity.before, activity.after))
    }
}

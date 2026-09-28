package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.repository.ProgressRepository
import javax.inject.Inject

class RememberLastNote @Inject constructor(private val progress: ProgressRepository) {

    suspend operator fun invoke(note: NoteId) {
        progress.setLastNote(note)
    }
}

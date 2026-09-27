package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import kotlin.time.Duration

data class ReadingAhead(val firstNote: NoteId, val noteCount: Int, val readingTime: Duration)

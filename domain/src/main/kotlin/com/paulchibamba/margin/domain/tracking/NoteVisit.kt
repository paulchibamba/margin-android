package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.model.NoteId

data class NoteVisit(val noteId: NoteId, val words: Int, val via: NoteOpenVia, val hasImages: Boolean)

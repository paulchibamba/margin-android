package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteOutline

data class ReadNote(val note: NoteOutline, val bookTitle: String, val concepts: List<Concept>)

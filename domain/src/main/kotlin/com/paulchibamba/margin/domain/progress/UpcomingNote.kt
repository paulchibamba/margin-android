package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteOutline

data class UpcomingNote(val note: NoteOutline, val notesAway: Int, val concepts: List<Concept>)

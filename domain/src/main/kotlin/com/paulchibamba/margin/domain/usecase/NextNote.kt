package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteOutline

data class NextNote(val outline: NoteOutline, val chapterTitle: String, val unlockedPosts: Int)

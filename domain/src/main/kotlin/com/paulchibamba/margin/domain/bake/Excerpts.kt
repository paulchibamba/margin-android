package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.progress.QuotePicker

object Excerpts {
    const val MAX_SENTENCES = 3

    fun quoteCandidates(noteText: String): String =
        QuotePicker.candidates(noteText).take(MAX_SENTENCES).joinToString(" ")

    fun opening(noteText: String): String = QuotePicker.sentencesOf(noteText).take(MAX_SENTENCES).joinToString(" ")
}

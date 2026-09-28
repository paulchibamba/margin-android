package com.paulchibamba.margin.navigation

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import kotlinx.serialization.Serializable

sealed interface MarginDestination {

    @Serializable
    data object Feed : MarginDestination

    @Serializable
    data object Read : MarginDestination

    @Serializable
    data class Book(val slug: String) : MarginDestination {
        val bookSlug: BookSlug get() = BookSlug(slug)

        companion object {
            fun of(bookSlug: BookSlug) = Book(bookSlug.value)
        }
    }

    @Serializable
    data class Note(val noteId: String, val fromPost: String? = null) : MarginDestination {
        val note: NoteId get() = NoteId(noteId)
        val fromPostId: PostId? get() = fromPost?.let(::PostId)

        companion object {
            fun of(note: NoteId, fromPost: PostId? = null) = Note(note.value, fromPost?.value)
        }
    }

    @Serializable
    data object Settings : MarginDestination

    @Serializable
    data object ReadingOnlyChapters : MarginDestination

    @Serializable
    data object Stats : MarginDestination
}

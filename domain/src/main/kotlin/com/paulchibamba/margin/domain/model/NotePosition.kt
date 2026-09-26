package com.paulchibamba.margin.domain.model

data class NotePosition(val chapter: Int, val order: Int) : Comparable<NotePosition> {

    override fun compareTo(other: NotePosition): Int =
        compareValuesBy(this, other, NotePosition::chapter, NotePosition::order)
}

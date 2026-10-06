package com.paulchibamba.margin.domain.drop

sealed interface DropEvidence {
    val count: Int

    data class Remembered(override val count: Int) : DropEvidence
    data class Passed(override val count: Int) : DropEvidence
    data class Introduced(override val count: Int) : DropEvidence
    data class PostsSeen(override val count: Int) : DropEvidence
}

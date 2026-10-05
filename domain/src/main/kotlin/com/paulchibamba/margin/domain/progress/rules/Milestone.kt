package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.model.ConceptId
import java.time.LocalDate

internal data class Milestone(
    val scope: String,
    val name: String,
    val book: String,
    val kind: String,
    val introduced: Int,
    val remembered: Int,
    val conceptIds: List<ConceptId>,
    val firstIntroducedOn: LocalDate?,
)

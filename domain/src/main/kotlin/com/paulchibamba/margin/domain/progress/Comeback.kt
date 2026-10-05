package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import java.time.LocalDate

data class Comeback(val concept: Concept, val firstFailOn: LocalDate, val failCount: Int, val passCount: Int)

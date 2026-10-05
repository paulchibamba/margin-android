package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import java.time.Instant

data class Struggle(val concept: Concept, val trigger: StruggleTrigger, val at: Instant)

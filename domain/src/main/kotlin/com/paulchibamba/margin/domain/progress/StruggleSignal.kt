package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import java.time.Instant

internal data class StruggleSignal(val conceptId: ConceptId, val trigger: StruggleTrigger, val at: Instant)

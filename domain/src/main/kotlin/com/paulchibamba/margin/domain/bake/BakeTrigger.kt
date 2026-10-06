package com.paulchibamba.margin.domain.bake

enum class BakeTrigger(val bakesRewards: Boolean, val needsNewFacts: Boolean) {
    SCHEDULED(bakesRewards = true, needsNewFacts = false),
    SESSION_END(bakesRewards = true, needsNewFacts = true),
    RE_EXPLAIN(bakesRewards = false, needsNewFacts = false),
    MANUAL(bakesRewards = true, needsNewFacts = false),
}

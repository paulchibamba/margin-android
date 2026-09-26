package com.paulchibamba.margin.domain.model

import com.paulchibamba.margin.domain.model.PostRole.TEACH
import com.paulchibamba.margin.domain.model.PostRole.TEST

enum class Format(val role: PostRole, val isInteractive: Boolean) {
    CAROUSEL(TEACH, isInteractive = true),
    FACT(TEACH, isInteractive = false),
    TIP(TEACH, isInteractive = false),
    ANALOGY(TEACH, isInteractive = false),
    DIALOGUE(TEACH, isInteractive = false),
    VERSUS(TEACH, isInteractive = false),
    MYTH(TEACH, isInteractive = true),
    CHECKLIST(TEACH, isInteractive = true),
    CODE_EXAMPLE(TEACH, isInteractive = false),
    MEME(TEACH, isInteractive = false),
    SOURCE(TEACH, isInteractive = false),
    MCQ(TEST, isInteractive = true),
    TRUE_FALSE(TEST, isInteractive = true),
    RECALL(TEST, isInteractive = true),
    FILL_BLANK(TEST, isInteractive = true),
    SPOT_BUG(TEST, isInteractive = true),
    SCENARIO(TEST, isInteractive = true),
}

package com.paulchibamba.margin.domain.model

import com.paulchibamba.margin.domain.model.Format.*
import com.paulchibamba.margin.domain.model.PostRole.TEACH
import com.paulchibamba.margin.domain.model.PostRole.TEST
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTest {

    private val expectedRoles = mapOf(
        CAROUSEL to TEACH, FACT to TEACH, TIP to TEACH, ANALOGY to TEACH, DIALOGUE to TEACH, VERSUS to TEACH,
        MYTH to TEACH, CHECKLIST to TEACH, CODE_EXAMPLE to TEACH, MEME to TEACH, SOURCE to TEACH,
        MCQ to TEST, TRUE_FALSE to TEST, RECALL to TEST, FILL_BLANK to TEST, SPOT_BUG to TEST, SCENARIO to TEST,
    )

    private val interactiveFormats = setOf(
        CAROUSEL, MYTH, CHECKLIST, MCQ, TRUE_FALSE, RECALL, FILL_BLANK, SPOT_BUG, SCENARIO,
    )

    @Test
    fun `every format has the role the spec gives it`() {
        assertEquals(expectedRoles, Format.entries.associateWith(Format::role))
    }

    @Test
    fun `exactly the formats you can act on are interactive`() {
        assertEquals(interactiveFormats, Format.entries.filter(Format::isInteractive).toSet())
    }
}

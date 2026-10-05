package com.paulchibamba.margin.domain.progress

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConceptRelationsTest {
    private val relations = ConceptRelations(allConcepts)

    @Test
    fun `concepts in the same section of a book are related`() {
        assertTrue(relations.areRelated(sqlInjection, parameterizedQueries))
        assertTrue(relations.areRelated(leastPrivilege, defenceInDepth))
    }

    @Test
    fun `concepts whose titles share an uncommon word are related, across books too`() {
        assertTrue(relations.areRelated(leastPrivilege, needToKnow))
        assertTrue(relations.areRelated(leastPrivilege, privilegeInBrowsers))
    }

    @Test
    fun `concepts with nothing in common are not related, and a concept is not related to itself`() {
        assertFalse(relations.areRelated(cia, storedXss))
        assertFalse(relations.areRelated(cia, cia))
    }

    @Test
    fun `a word shared by many titles does not make concepts related`() {
        val common = (1..5).map { order -> conceptOf(browsers, order, "Origin Rule $order", "Section $order") }
        val crowded = ConceptRelations(common)

        assertFalse(crowded.areRelated(common[0], common[1]))
    }

    @Test
    fun `generic words like security do not make concepts related`() {
        val first = conceptOf(fundamentals, 0, "Usable Security", "One")
        val second = conceptOf(browsers, 0, "Security Headers", "Two")

        assertFalse(ConceptRelations(listOf(first, second)).areRelated(first, second))
    }
}

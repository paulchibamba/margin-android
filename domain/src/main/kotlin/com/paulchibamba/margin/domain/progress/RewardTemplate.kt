package com.paulchibamba.margin.domain.progress

class RewardTemplate(
    private val title: String,
    private val body: String,
    private val appliesWhen: (Map<String, String>) -> Boolean = { true },
) {
    fun fill(facts: Map<String, String>): RewardDraft? {
        if (!appliesWhen(facts)) return null
        val filledTitle = fillIn(title, facts) ?: return null
        val filledBody = fillIn(body, facts) ?: return null
        return RewardDraft(filledTitle, filledBody)
    }

    private fun fillIn(text: String, facts: Map<String, String>): String? {
        val keys = PLACEHOLDER.findAll(text).map { match -> match.groupValues[1] }.toList()
        if (keys.any { key -> key !in facts }) return null
        return PLACEHOLDER.replace(text) { match -> facts.getValue(match.groupValues[1]) }
    }

    private companion object {
        val PLACEHOLDER = Regex("""\{(\w+)\}""")
    }
}

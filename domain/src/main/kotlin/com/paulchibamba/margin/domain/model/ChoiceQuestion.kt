package com.paulchibamba.margin.domain.model

interface ChoiceQuestion {
    val question: String
    val options: List<String>
    val answerIndex: Int
    val explanation: String

    fun isCorrect(optionIndex: Int): Boolean = optionIndex == answerIndex
}

internal fun ChoiceQuestion.requireAnswerAmongOptions() {
    require(answerIndex in options.indices) {
        "Answer index $answerIndex is outside ${options.size} options"
    }
}

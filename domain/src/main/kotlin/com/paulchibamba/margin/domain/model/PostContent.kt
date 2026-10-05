package com.paulchibamba.margin.domain.model

import com.paulchibamba.margin.domain.progress.RewardKind

sealed interface PostContent {
    val format: Format
    val title: String
    val readableText: List<String>

    fun wordCount(): Int = readableText.sumOf(::countWords)

    data class Carousel(override val title: String, val slides: List<String>) : PostContent {
        init { require(slides.isNotEmpty()) { "A carousel needs at least one slide" } }
        override val format get() = Format.CAROUSEL
        override val readableText get() = listOf(title) + slides
    }

    data class Fact(override val title: String, val text: String) : PostContent {
        override val format get() = Format.FACT
        override val readableText get() = listOf(title, text)
    }

    data class Tip(override val title: String, val text: String) : PostContent {
        override val format get() = Format.TIP
        override val readableText get() = listOf(title, text)
    }

    data class Analogy(override val title: String, val text: String) : PostContent {
        override val format get() = Format.ANALOGY
        override val readableText get() = listOf(title, text)
    }

    data class Dialogue(override val title: String, val lines: List<String>) : PostContent {
        init { require(lines.isNotEmpty()) { "A dialogue needs at least one line" } }
        override val format get() = Format.DIALOGUE
        override val readableText get() = listOf(title) + lines
    }

    data class Versus(override val title: String, val left: String, val right: String) : PostContent {
        override val format get() = Format.VERSUS
        override val readableText get() = listOf(title, left, right)
    }

    data class Myth(override val title: String, val myth: String, val reality: String) : PostContent {
        override val format get() = Format.MYTH
        override val readableText get() = listOf(title, myth, reality)
    }

    data class Checklist(override val title: String, val items: List<String>) : PostContent {
        init { require(items.isNotEmpty()) { "A checklist needs at least one item" } }
        override val format get() = Format.CHECKLIST
        override val readableText get() = listOf(title) + items
    }

    data class CodeExample(override val title: String, val code: String, val caption: String?) : PostContent {
        override val format get() = Format.CODE_EXAMPLE
        override val readableText get() = listOfNotNull(title, caption, code)
    }

    data class Meme(
        override val title: String,
        val imagePath: String,
        val caption: String,
        val imageText: List<String>,
    ) : PostContent {
        override val format get() = Format.MEME
        override val readableText get() = listOf(title, caption)
    }

    data class Progress(
        val kind: RewardKind,
        override val title: String,
        val body: String,
        val sourceLine: String?,
        val noteId: NoteId?,
    ) : PostContent {
        override val format get() = if (kind.isSupport) Format.RE_EXPLAIN else Format.PROGRESS
        override val readableText get() = listOfNotNull(title, body, sourceLine)
    }

    data class Source(override val title: String, val excerpt: String, val noteId: NoteId) : PostContent {
        override val format get() = Format.SOURCE
        override val readableText get() = listOf(title, excerpt)
    }

    data class Mcq(
        override val title: String,
        override val question: String,
        override val options: List<String>,
        override val answerIndex: Int,
        override val explanation: String,
    ) : PostContent, ChoiceQuestion {
        init { requireAnswerAmongOptions() }
        override val format get() = Format.MCQ
        override val readableText get() = listOf(title, question) + options
    }

    data class TrueFalse(
        override val title: String,
        val statement: String,
        val isTrue: Boolean,
        val explanation: String,
    ) : PostContent {
        override val format get() = Format.TRUE_FALSE
        override val readableText get() = listOf(title, statement, "True", "False")
    }

    data class Recall(override val title: String, val question: String, val answer: String) : PostContent {
        override val format get() = Format.RECALL
        override val readableText get() = listOf(title, question)
    }

    data class FillBlank(
        override val title: String,
        val sentence: String,
        val answer: String,
        val explanation: String?,
    ) : PostContent {
        override val format get() = Format.FILL_BLANK
        override val readableText get() = listOf(title, sentence)
    }

    data class SpotBug(
        override val title: String,
        val code: String,
        override val question: String,
        override val options: List<String>,
        override val answerIndex: Int,
        override val explanation: String,
    ) : PostContent, ChoiceQuestion {
        init { requireAnswerAmongOptions() }
        override val format get() = Format.SPOT_BUG
        override val readableText get() = listOf(title, question) + options + code
    }

    data class Scenario(
        override val title: String,
        val situation: String,
        override val question: String,
        override val options: List<String>,
        override val answerIndex: Int,
        override val explanation: String,
    ) : PostContent, ChoiceQuestion {
        init { requireAnswerAmongOptions() }
        override val format get() = Format.SCENARIO
        override val readableText get() = listOf(title, situation, question) + options
    }
}

private val WHITESPACE = Regex("""\s+""")

private fun countWords(text: String): Int = text.split(WHITESPACE).count(String::isNotBlank)

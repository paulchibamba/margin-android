package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.model.PostContent
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object PackPostContent {

    fun from(post: JsonObject): PostContent {
        val title = post.string("title")
        return when (val format = post.string("format")) {
            "carousel" -> PostContent.Carousel(title, post.slides())
            "fact" -> PostContent.Fact(title, post.slide(0))
            "tip" -> PostContent.Tip(title, post.slide(0))
            "analogy" -> PostContent.Analogy(title, post.slide(0))
            "dialogue" -> PostContent.Dialogue(title, post.slides())
            "versus" -> PostContent.Versus(title, post.slide(0), post.slide(1))
            "myth" -> PostContent.Myth(title, post.slide(0), post.slide(1))
            "checklist" -> PostContent.Checklist(title, post.slides())
            "code_example" -> PostContent.CodeExample(title, post.string("code"), post.slides().firstOrNull())
            "meme" -> meme(title, post)
            else -> testContent(format, title, post)
        }
    }

    private fun testContent(format: String, title: String, post: JsonObject): PostContent = when (format) {
        "mcq" -> PostContent.Mcq(title, post.string("question"), post.options(), post.answerIndex(), post.why())
        "true_false" -> trueFalse(title, post)
        "recall" -> PostContent.Recall(title, post.string("question"), post.string("answer"))
        "fill_blank" -> fillBlank(title, post)
        "spot_bug" -> spotBug(title, post)
        "scenario" -> scenario(title, post)
        else -> error("Unknown format '$format'")
    }

    private fun meme(title: String, post: JsonObject) = PostContent.Meme(
        title = title,
        imagePath = post.string("image"),
        caption = post.slides().firstOrNull().orEmpty(),
        imageText = post.strings("meme_lines"),
    )

    private fun trueFalse(title: String, post: JsonObject) =
        PostContent.TrueFalse(title, post.string("question"), isTrue = post.answerIndex() == 0, post.why())

    private fun fillBlank(title: String, post: JsonObject) =
        PostContent.FillBlank(title, post.string("question"), post.string("answer"), post.optionalString("explanation"))

    private fun spotBug(title: String, post: JsonObject) = PostContent.SpotBug(
        title, post.string("code"), post.string("question"), post.options(), post.answerIndex(), post.why(),
    )

    private fun scenario(title: String, post: JsonObject) = PostContent.Scenario(
        title, post.slide(0), post.string("question"), post.options(), post.answerIndex(), post.why(),
    )

    private fun JsonObject.slides(): List<String> = strings("slides")
    private fun JsonObject.slide(index: Int): String = slides()[index]
    private fun JsonObject.options(): List<String> = strings("options")
    private fun JsonObject.answerIndex(): Int = int("answer_index")
    private fun JsonObject.why(): String = string("explanation")
}

internal fun JsonObject.string(key: String): String = getValue(key).jsonPrimitive.content

internal fun JsonObject.int(key: String): Int = getValue(key).jsonPrimitive.int

internal fun JsonObject.optionalString(key: String): String? = get(key)?.jsonPrimitive?.content

internal fun JsonObject.strings(key: String): List<String> =
    get(key)?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()

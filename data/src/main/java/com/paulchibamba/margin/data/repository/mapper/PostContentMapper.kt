package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.pack.PostDto
import com.paulchibamba.margin.data.pack.decodePost
import com.paulchibamba.margin.domain.model.PostContent

object PostContentMapper {

    fun fromJson(json: String): PostContent = fromDto(decodePost(json))

    fun fromDto(post: PostDto): PostContent {
        val title = post.title.orEmpty()
        return when (val format = post.format) {
            "carousel" -> PostContent.Carousel(title, post.slides())
            "fact" -> PostContent.Fact(title, post.slide(0))
            "tip" -> PostContent.Tip(title, post.slide(0))
            "analogy" -> PostContent.Analogy(title, post.slide(0))
            "dialogue" -> PostContent.Dialogue(title, post.slides())
            "versus" -> PostContent.Versus(title, post.slide(0), post.slide(1))
            "myth" -> PostContent.Myth(title, post.slide(0), post.slide(1))
            "checklist" -> PostContent.Checklist(title, post.slides())
            "code_example" -> PostContent.CodeExample(title, post.code.orEmpty(), post.slides.orEmpty().firstOrNull())
            "meme" -> meme(title, post)
            else -> testContent(format, title, post)
        }
    }

    private fun testContent(format: String?, title: String, post: PostDto): PostContent = when (format) {
        "mcq" -> PostContent.Mcq(title, post.question(), post.options(), post.answerIndex(), post.why())
        "true_false" -> PostContent.TrueFalse(title, post.question(), post.answerIndex() == 0, post.why())
        "recall" -> PostContent.Recall(title, post.question(), post.answer.orEmpty())
        "fill_blank" -> PostContent.FillBlank(title, post.question(), post.answer.orEmpty(), post.explanation)
        "spot_bug" -> spotBug(title, post)
        "scenario" -> scenario(title, post)
        else -> throw IllegalArgumentException("Unknown post format '$format' in ${post.id}")
    }

    private fun meme(title: String, post: PostDto) = PostContent.Meme(
        title = title,
        imagePath = post.image.orEmpty(),
        caption = post.slides.orEmpty().firstOrNull().orEmpty(),
        imageText = post.memeLines.orEmpty(),
    )

    private fun spotBug(title: String, post: PostDto) = PostContent.SpotBug(
        title, post.code.orEmpty(), post.question(), post.options(), post.answerIndex(), post.why(),
    )

    private fun scenario(title: String, post: PostDto) = PostContent.Scenario(
        title, post.slide(0), post.question(), post.options(), post.answerIndex(), post.why(),
    )

    private fun PostDto.slides(): List<String> = slides.orEmpty()
    private fun PostDto.slide(index: Int): String = slides().getOrElse(index) { "" }
    private fun PostDto.question(): String = question.orEmpty()
    private fun PostDto.options(): List<String> = options.orEmpty()
    private fun PostDto.answerIndex(): Int = answerIndex ?: 0
    private fun PostDto.why(): String = explanation.orEmpty()
}

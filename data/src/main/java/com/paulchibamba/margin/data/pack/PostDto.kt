package com.paulchibamba.margin.data.pack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String? = null,
    val format: String? = null,
    val role: String? = null,
    val title: String? = null,
    val slides: List<String>? = null,
    val code: String? = null,
    val question: String? = null,
    val options: List<String>? = null,
    @SerialName("answer_index") val answerIndex: Int? = null,
    val answer: String? = null,
    val explanation: String? = null,
    val image: String? = null,
    @SerialName("meme_template") val memeTemplate: String? = null,
    @SerialName("meme_lines") val memeLines: List<String>? = null,
)

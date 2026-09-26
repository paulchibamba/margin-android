package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.PostContent

fun tipWithWords(wordCount: Int) =
    PostContent.Tip(title = "Tip", text = List(wordCount - 1) { "word" }.joinToString(" "))

fun checklistWithWords(wordCount: Int) =
    PostContent.Checklist(title = "Checklist", items = List(wordCount - 1) { "item" })

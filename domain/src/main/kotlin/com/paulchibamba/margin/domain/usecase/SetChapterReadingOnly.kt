package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class SetChapterReadingOnly @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(chapter: ChapterRef, isReadingOnly: Boolean) {
        val current = settings.readingOnlyChapters().chaptersOf(chapter.bookSlug)
        val updated = if (isReadingOnly) current + chapter.chapter else current - chapter.chapter
        settings.setReadingOnlyChapters(chapter.bookSlug, updated)
    }
}

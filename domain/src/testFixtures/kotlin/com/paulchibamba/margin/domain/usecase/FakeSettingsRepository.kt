package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.defaultSettings
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSettingsRepository(settings: List<BookSettings> = defaultSettings) : SettingsRepository {
    val bookSettings = MutableStateFlow(settings)
    val readingOnly = MutableStateFlow(mapOf(appSec.slug to setOf(2)))
    val retention = MutableStateFlow(0.9)

    override suspend fun bookSettings() = bookSettings.value
    override fun observeBookSettings(): Flow<List<BookSettings>> = bookSettings
    override suspend fun saveBookSettings(settings: BookSettings) {
        bookSettings.value = bookSettings.value.map { if (it.bookSlug == settings.bookSlug) settings else it }
    }

    override suspend fun readingOnlyChapters() = ReadingOnlyChapters(readingOnly.value)
    override fun observeReadingOnlyChapters(): Flow<ReadingOnlyChapters> = readingOnly.map(::ReadingOnlyChapters)
    override suspend fun setReadingOnlyChapters(book: BookSlug, chapters: Set<Int>) {
        readingOnly.value += book to chapters
    }

    override suspend fun desiredRetention() = retention.value
    override fun observeDesiredRetention(): Flow<Double> = retention
    override suspend fun setDesiredRetention(retention: Double) {
        this.retention.value = retention
    }
}

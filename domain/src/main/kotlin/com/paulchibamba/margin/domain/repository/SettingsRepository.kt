package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun bookSettings(): List<BookSettings>
    fun observeBookSettings(): Flow<List<BookSettings>>
    suspend fun saveBookSettings(settings: BookSettings)

    suspend fun readingOnlyChapters(): ReadingOnlyChapters
    fun observeReadingOnlyChapters(): Flow<ReadingOnlyChapters>
    suspend fun setReadingOnlyChapters(book: BookSlug, chapters: Set<Int>)

    suspend fun desiredRetention(): Double
    fun observeDesiredRetention(): Flow<Double>
    suspend fun setDesiredRetention(retention: Double)

    suspend fun isReviewReminderOn(): Boolean
    fun observeReviewReminder(): Flow<Boolean>
    suspend fun setReviewReminder(isOn: Boolean)

    suspend fun darkMode(): DarkMode
    fun observeDarkMode(): Flow<DarkMode>
    suspend fun setDarkMode(mode: DarkMode)

    suspend fun isDarkPostsOn(): Boolean
    fun observeDarkPosts(): Flow<Boolean>
    suspend fun setDarkPosts(isOn: Boolean)
}

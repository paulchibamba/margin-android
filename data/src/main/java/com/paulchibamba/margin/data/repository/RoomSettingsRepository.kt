package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.repository.mapper.SettingsMapper
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.memory.DesiredRetention
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSettingsRepository @Inject constructor(
    private val database: MarginDatabase,
    private val startup: StartupInitializer,
    private val events: EventRecorder,
) : SettingsRepository {
    private val settingsDao get() = database.settingsDao()

    override suspend fun bookSettings(): List<BookSettings> = observeBookSettings().first()

    override fun observeBookSettings(): Flow<List<BookSettings>> = settingsDao.bookSettings()
        .onStart { startup.ensureImported() }
        .map { settings -> settings.map(SettingsMapper::toDomain) }

    override suspend fun saveBookSettings(settings: BookSettings) {
        val entity = SettingsMapper.toEntity(settings)
        val old = settingsDao.bookSettingsOf(entity.bookSlug)
        settingsDao.upsertBookSettings(entity)
        recordChange("book_active:${entity.bookSlug}", old?.active?.toString(), entity.active.toString())
        recordChange("book_priority:${entity.bookSlug}", old?.priority, entity.priority)
    }

    override suspend fun readingOnlyChapters(): ReadingOnlyChapters = observeReadingOnlyChapters().first()

    override fun observeReadingOnlyChapters(): Flow<ReadingOnlyChapters> = settingsDao.readingOnlyChapters()
        .onStart { startup.ensureImported() }
        .map(SettingsMapper::readingOnly)

    override suspend fun setReadingOnlyChapters(book: BookSlug, chapters: Set<Int>) {
        val old = settingsDao.readingOnlyChaptersOf(book.value)
        settingsDao.replaceReadingOnlyChapters(book.value, chapters.sorted())
        recordChange("reading_only:${book.value}", old.joinToString(","), chapters.sorted().joinToString(","))
    }

    override suspend fun desiredRetention(): Double = observeDesiredRetention().first()

    override fun observeDesiredRetention(): Flow<Double> = database.metaDao().observe(MetaKey.DESIRED_RETENTION)
        .map { stored -> stored?.toDoubleOrNull() ?: DesiredRetention.DEFAULT }

    override suspend fun setDesiredRetention(retention: Double) {
        putSetting(MetaKey.DESIRED_RETENTION, retention.toString())
    }

    override suspend fun isReviewReminderOn(): Boolean = observeReviewReminder().first()

    override fun observeReviewReminder(): Flow<Boolean> = database.metaDao().observe(MetaKey.REVIEW_REMINDER)
        .map { stored -> stored.toBoolean() }

    override suspend fun setReviewReminder(isOn: Boolean) {
        putSetting(MetaKey.REVIEW_REMINDER, isOn.toString())
    }

    override suspend fun darkMode(): DarkMode = observeDarkMode().first()

    override fun observeDarkMode(): Flow<DarkMode> = database.metaDao().observe(MetaKey.DARK_MODE)
        .map(DarkMode::fromName)

    override suspend fun setDarkMode(mode: DarkMode) {
        putSetting(MetaKey.DARK_MODE, mode.name)
    }

    override suspend fun isDarkPostsOn(): Boolean = observeDarkPosts().first()

    override fun observeDarkPosts(): Flow<Boolean> = database.metaDao().observe(MetaKey.DARK_POSTS)
        .map { stored -> stored.toBoolean() }

    override suspend fun setDarkPosts(isOn: Boolean) {
        putSetting(MetaKey.DARK_POSTS, isOn.toString())
    }

    private suspend fun putSetting(key: String, value: String) {
        val old = database.metaDao().get(key)
        database.metaDao().put(listOf(MetaEntity(key, value)))
        recordChange(key, old, value)
    }

    private fun recordChange(key: String, old: String?, new: String) {
        if (old != new) events.record(Event.SettingChanged(key, old, new))
    }
}

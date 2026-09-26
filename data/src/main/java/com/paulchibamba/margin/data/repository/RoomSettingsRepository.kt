package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.repository.mapper.SettingsMapper
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.repository.SettingsRepository
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
) : SettingsRepository {
    private val settingsDao get() = database.settingsDao()

    override suspend fun bookSettings(): List<BookSettings> = observeBookSettings().first()

    override fun observeBookSettings(): Flow<List<BookSettings>> = settingsDao.bookSettings()
        .onStart { startup.ensureImported() }
        .map { settings -> settings.map(SettingsMapper::toDomain) }

    override suspend fun saveBookSettings(settings: BookSettings) {
        settingsDao.upsertBookSettings(SettingsMapper.toEntity(settings))
    }

    override suspend fun readingOnlyChapters(): ReadingOnlyChapters = observeReadingOnlyChapters().first()

    override fun observeReadingOnlyChapters(): Flow<ReadingOnlyChapters> = settingsDao.readingOnlyChapters()
        .onStart { startup.ensureImported() }
        .map(SettingsMapper::readingOnly)

    override suspend fun setReadingOnlyChapters(book: BookSlug, chapters: Set<Int>) {
        settingsDao.replaceReadingOnlyChapters(book.value, chapters.sorted())
    }

    override suspend fun desiredRetention(): Double =
        database.metaDao().get(MetaKey.DESIRED_RETENTION)?.toDoubleOrNull() ?: DEFAULT_RETENTION

    override suspend fun setDesiredRetention(retention: Double) {
        database.metaDao().put(listOf(MetaEntity(MetaKey.DESIRED_RETENTION, retention.toString())))
    }

    private companion object {
        const val DEFAULT_RETENTION = 0.9
    }
}

package com.paulchibamba.margin.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.paulchibamba.margin.data.database.dao.ActivityDao
import com.paulchibamba.margin.data.database.dao.BookCoverDao
import com.paulchibamba.margin.data.database.dao.ConceptProgressDao
import com.paulchibamba.margin.data.database.dao.ContentDao
import com.paulchibamba.margin.data.database.dao.EventDao
import com.paulchibamba.margin.data.database.dao.FeedLogDao
import com.paulchibamba.margin.data.database.dao.FeedStateDao
import com.paulchibamba.margin.data.database.dao.MetaDao
import com.paulchibamba.margin.data.database.dao.ProgressResetDao
import com.paulchibamba.margin.data.database.dao.ReadingDao
import com.paulchibamba.margin.data.database.dao.SessionTallyDao
import com.paulchibamba.margin.data.database.dao.SettingsDao
import com.paulchibamba.margin.data.database.entity.ActionLogEntity
import com.paulchibamba.margin.data.database.entity.BookCoverEntity
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.ConceptProgressEntity
import com.paulchibamba.margin.data.database.entity.DailyActivityEntity
import com.paulchibamba.margin.data.database.entity.EventEntity
import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.data.database.entity.FormatAffinityEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.entity.PostEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import com.paulchibamba.margin.data.database.entity.ReviewLogEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity

@Database(
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
    entities = [
        BookEntity::class,
        ChapterEntity::class,
        ConceptEntity::class,
        PostEntity::class,
        NoteEntity::class,
        ConceptProgressEntity::class,
        ReviewLogEntity::class,
        PostSeenEntity::class,
        ActionLogEntity::class,
        FormatAffinityEntity::class,
        NoteReadEntity::class,
        ChapterKnownEntity::class,
        BookSettingsEntity::class,
        ReadingOnlyChapterEntity::class,
        SavedPostEntity::class,
        MetaEntity::class,
        FeedHistoryEntity::class,
        DailyActivityEntity::class,
        BookCoverEntity::class,
        EventEntity::class,
    ],
)
abstract class MarginDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun conceptProgressDao(): ConceptProgressDao
    abstract fun feedStateDao(): FeedStateDao
    abstract fun feedLogDao(): FeedLogDao
    abstract fun readingDao(): ReadingDao
    abstract fun settingsDao(): SettingsDao
    abstract fun metaDao(): MetaDao
    abstract fun activityDao(): ActivityDao
    abstract fun bookCoverDao(): BookCoverDao
    abstract fun progressResetDao(): ProgressResetDao
    abstract fun eventDao(): EventDao
    abstract fun sessionTallyDao(): SessionTallyDao

    companion object {
        const val NAME = "margin.db"
    }
}

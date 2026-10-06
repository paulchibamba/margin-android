package com.paulchibamba.margin.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.paulchibamba.margin.data.database.dao.ActivityDao
import com.paulchibamba.margin.data.database.dao.BookCoverDao
import com.paulchibamba.margin.data.database.dao.ConceptProgressDao
import com.paulchibamba.margin.data.database.dao.ContentDao
import com.paulchibamba.margin.data.database.dao.DailyDropDao
import com.paulchibamba.margin.data.database.dao.EventDao
import com.paulchibamba.margin.data.database.dao.FeedLogDao
import com.paulchibamba.margin.data.database.dao.FeedStateDao
import com.paulchibamba.margin.data.database.dao.GeneratedPostDao
import com.paulchibamba.margin.data.database.dao.LlmCallDao
import com.paulchibamba.margin.data.database.dao.MetaDao
import com.paulchibamba.margin.data.database.dao.ProgressResetDao
import com.paulchibamba.margin.data.database.dao.ReadingDao
import com.paulchibamba.margin.data.database.dao.RollupDao
import com.paulchibamba.margin.data.database.dao.ScreenTimeDao
import com.paulchibamba.margin.data.database.dao.SessionTallyDao
import com.paulchibamba.margin.data.database.dao.SettingsDao
import com.paulchibamba.margin.data.database.entity.ActionLogEntity
import com.paulchibamba.margin.data.database.entity.AppCategoryOverrideEntity
import com.paulchibamba.margin.data.database.entity.BookCoverEntity
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.ConceptProgressEntity
import com.paulchibamba.margin.data.database.entity.DailyDropEntity
import com.paulchibamba.margin.data.database.entity.DailyRollupEntity
import com.paulchibamba.margin.data.database.entity.DailyActivityEntity
import com.paulchibamba.margin.data.database.entity.EventEntity
import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.data.database.entity.FormatAffinityEntity
import com.paulchibamba.margin.data.database.entity.GeneratedPostEntity
import com.paulchibamba.margin.data.database.entity.LlmCallEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.entity.PostEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import com.paulchibamba.margin.data.database.entity.ReviewLogEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity
import com.paulchibamba.margin.data.database.entity.ScreenTimeDailyEntity

@Database(
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
    ],
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
        DailyRollupEntity::class,
        ScreenTimeDailyEntity::class,
        AppCategoryOverrideEntity::class,
        GeneratedPostEntity::class,
        LlmCallEntity::class,
        DailyDropEntity::class,
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
    abstract fun rollupDao(): RollupDao
    abstract fun screenTimeDao(): ScreenTimeDao
    abstract fun generatedPostDao(): GeneratedPostDao
    abstract fun llmCallDao(): LlmCallDao
    abstract fun dailyDropDao(): DailyDropDao

    companion object {
        const val NAME = "margin.db"
    }
}

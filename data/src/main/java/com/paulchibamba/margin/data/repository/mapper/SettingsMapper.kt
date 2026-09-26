package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.DailyActivityEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.rewards.DailyActivity
import java.time.LocalDate

object SettingsMapper {

    fun toDomain(entity: BookSettingsEntity) =
        BookSettings(BookSlug(entity.bookSlug), entity.active, Priority.valueOf(entity.priority.uppercase()))

    fun toEntity(settings: BookSettings) =
        BookSettingsEntity(settings.bookSlug.value, settings.isActive, settings.priority.name.lowercase())

    fun readingOnly(entities: List<ReadingOnlyChapterEntity>) = ReadingOnlyChapters(
        entities.groupBy({ BookSlug(it.bookSlug) }, ReadingOnlyChapterEntity::chapter).mapValues { it.value.toSet() },
    )

    fun toDomain(entity: DailyActivityEntity) =
        DailyActivity(LocalDate.parse(entity.date), entity.postsSeen, entity.notesRead)

    fun toEntity(activity: DailyActivity) =
        DailyActivityEntity(activity.date.toString(), activity.postsSeen, activity.notesRead)
}

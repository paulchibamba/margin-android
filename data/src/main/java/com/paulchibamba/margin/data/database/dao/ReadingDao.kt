package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markNoteRead(noteRead: NoteReadEntity)

    @Query("SELECT * FROM note_read")
    fun notesRead(): Flow<List<NoteReadEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markChapterKnown(chapterKnown: ChapterKnownEntity)

    @Query("SELECT * FROM chapter_known")
    fun chaptersKnown(): Flow<List<ChapterKnownEntity>>
}

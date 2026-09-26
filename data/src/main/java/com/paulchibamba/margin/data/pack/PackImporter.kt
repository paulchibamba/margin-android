package com.paulchibamba.margin.data.pack

import androidx.room.withTransaction
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import kotlinx.coroutines.CancellationException

class PackImporter(
    private val reader: PackReader,
    private val mapper: PackMapper,
    private val database: MarginDatabase,
    private val logger: ImportLogger,
) {

    suspend fun importIfChanged(): ImportResult = try {
        val bundledVersion = reader.readManifest().version
        if (bundledVersion == importedVersion()) skipped(bundledVersion) else import(mapper.map(reader.read()))
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Exception) {
        logger.error("Pack import failed; keeping the previous content", failure)
        ImportResult.Failed(failure)
    }

    private suspend fun importedVersion(): String? = database.metaDao().get(MetaKey.PACK_VERSION)

    private fun skipped(version: String): ImportResult {
        logger.info("Pack $version already imported; skipping")
        return ImportResult.Skipped(version)
    }

    private suspend fun import(pack: MappedPack): ImportResult {
        database.withTransaction {
            database.contentDao().replaceAll(pack.content)
            seedSettingsForNewBooks(pack)
            database.metaDao().put(listOf(MetaEntity(MetaKey.PACK_VERSION, pack.version)))
        }
        logImported(pack)
        return ImportResult.Imported(pack.version, pack.warnings)
    }

    private suspend fun seedSettingsForNewBooks(pack: MappedPack) {
        val newBooks = insertSettingsForBooksWithout(pack.defaultBookSettings)
        val readingOnlyDefaults = pack.defaultReadingOnlyChapters.filter { it.bookSlug in newBooks }
        database.settingsDao().insertReadingOnlyChapters(readingOnlyDefaults)
    }

    private suspend fun insertSettingsForBooksWithout(defaults: List<BookSettingsEntity>): Set<String> {
        val rowIds = database.settingsDao().insertBookSettingsIfAbsent(defaults)
        return defaults.zip(rowIds)
            .filter { (_, rowId) -> rowId != NOT_INSERTED }
            .map { (settings, _) -> settings.bookSlug }
            .toSet()
    }

    private fun logImported(pack: MappedPack) {
        val content = pack.content
        logger.info(
            "Imported pack ${pack.version}: ${content.books.size} books, ${content.concepts.size} concepts, " +
                "${content.posts.size} posts, ${content.notes.size} notes",
        )
        pack.warnings.conceptsWithoutTeachPost.forEach { logger.warn("Concept $it has no teach post") }
        pack.warnings.conceptsWithoutTestPost.forEach { logger.warn("Concept $it has no test post") }
    }

    private companion object {
        const val NOT_INSERTED = -1L
    }
}

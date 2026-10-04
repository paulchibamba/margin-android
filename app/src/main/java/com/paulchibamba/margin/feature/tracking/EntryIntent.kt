package com.paulchibamba.margin.feature.tracking

import android.content.Intent
import com.paulchibamba.margin.domain.tracking.SessionEntry

object EntryIntent {
    private const val EXTRA_ENTRY = "com.paulchibamba.margin.ENTRY"

    fun Intent.withEntry(entry: SessionEntry): Intent = putExtra(EXTRA_ENTRY, entry.name)

    fun entryOf(intent: Intent?): SessionEntry {
        val name = intent?.getStringExtra(EXTRA_ENTRY) ?: return SessionEntry.LAUNCHER
        return SessionEntry.entries.firstOrNull { it.name == name } ?: SessionEntry.LAUNCHER
    }
}

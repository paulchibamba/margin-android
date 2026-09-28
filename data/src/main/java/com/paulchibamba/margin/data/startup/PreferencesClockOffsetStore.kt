package com.paulchibamba.margin.data.startup

import android.content.Context
import com.paulchibamba.margin.domain.repository.ClockOffsetStore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PreferencesClockOffsetStore(context: Context) : ClockOffsetStore {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun load(): Duration = preferences.getLong(OFFSET_KEY, 0L).milliseconds

    override fun save(offset: Duration) {
        preferences.edit().putLong(OFFSET_KEY, offset.inWholeMilliseconds).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "debug_clock"
        const val OFFSET_KEY = "offset_millis"
    }
}

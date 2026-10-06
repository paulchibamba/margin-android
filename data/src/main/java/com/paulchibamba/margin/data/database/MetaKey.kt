package com.paulchibamba.margin.data.database

object MetaKey {
    const val PACK_VERSION = "pack_version"
    const val FEED_STEP = "feed_step"
    const val REWARD_AT = "reward_at"
    const val LEGACY_DELIGHT_AT = "delight_at"
    const val LAST_PREVIEW_AT = "last_preview_at"
    const val LAST_NOTE = "last_note"
    const val DESIRED_RETENTION = "desired_retention"
    const val REVIEW_REMINDER = "review_reminder"
    const val DARK_MODE = "dark_mode"
    const val DARK_POSTS = "dark_posts"
    const val SCREEN_TIME_INGESTED_THROUGH = "screen_time_ingested_through"
    const val SCREEN_TIME_INGEST_FROM = "screen_time_ingest_from"
    const val LLM_MODEL_BAKE = "llm_model_bake"
    const val LLM_DAILY_CAP_MICROS = "llm_daily_cap_micros"
    const val SEND_EXCERPTS = "send_excerpts"
    private const val BOOK_LAST_NEW = "book_last_new:"
    private const val BADGE_SHOWN = "badge_shown:"

    val PROGRESS_KEYS = listOf(FEED_STEP, REWARD_AT, LEGACY_DELIGHT_AT, LAST_PREVIEW_AT, LAST_NOTE)
    val PROGRESS_PREFIXES = listOf(BOOK_LAST_NEW, BADGE_SHOWN)

    fun bookLastNew(bookSlug: String): String = "$BOOK_LAST_NEW$bookSlug"

    fun badgeShown(bookSlug: String, kind: String): String = "$BADGE_SHOWN$bookSlug:$kind"
}

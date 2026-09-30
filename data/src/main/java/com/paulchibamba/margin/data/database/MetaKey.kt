package com.paulchibamba.margin.data.database

object MetaKey {
    const val PACK_VERSION = "pack_version"
    const val FEED_STEP = "feed_step"
    const val DELIGHT_AT = "delight_at"
    const val LAST_PREVIEW_AT = "last_preview_at"
    const val LAST_NOTE = "last_note"
    const val DESIRED_RETENTION = "desired_retention"
    const val REVIEW_REMINDER = "review_reminder"
    const val DARK_MODE = "dark_mode"
    const val DARK_POSTS = "dark_posts"
    private const val BOOK_LAST_NEW = "book_last_new:"
    private const val BADGE_SHOWN = "badge_shown:"

    val PROGRESS_KEYS = listOf(FEED_STEP, DELIGHT_AT, LAST_PREVIEW_AT, LAST_NOTE)
    val PROGRESS_PREFIXES = listOf(BOOK_LAST_NEW, BADGE_SHOWN)

    fun bookLastNew(bookSlug: String): String = "$BOOK_LAST_NEW$bookSlug"

    fun badgeShown(bookSlug: String, kind: String): String = "$BADGE_SHOWN$bookSlug:$kind"
}

package com.paulchibamba.margin.data.database

object MetaKey {
    const val PACK_VERSION = "pack_version"
    const val FEED_STEP = "feed_step"
    const val DELIGHT_AT = "delight_at"
    const val LAST_PREVIEW_AT = "last_preview_at"
    const val LAST_NOTE = "last_note"
    const val DESIRED_RETENTION = "desired_retention"
    const val REVIEW_REMINDER = "review_reminder"

    fun bookLastNew(bookSlug: String): String = "book_last_new:$bookSlug"

    fun badgeShown(bookSlug: String, kind: String): String = "badge_shown:$bookSlug:$kind"
}

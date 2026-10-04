package com.paulchibamba.margin.domain.tracking

enum class EventType(val key: String) {
    SESSION_START("session_start"),
    SESSION_END("session_end"),
    POST_IMPRESSION("post_impression"),
    POST_EXPOSURE("post_exposure"),
    POST_REVISIT("post_revisit"),
    POST_INTERACTION("post_interaction"),
    POST_ANSWER("post_answer"),
    POST_ACTION("post_action"),
    NOTE_OPEN("note_open"),
    NOTE_EXPOSURE("note_exposure"),
    IMAGE_ZOOM("image_zoom"),
    SETTING_CHANGED("setting_changed"),
}

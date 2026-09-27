package com.paulchibamba.margin.feature.feed.post

object SpeakerLineParser {

    private const val MAX_SPEAKER_LENGTH = 40
    private val SPEAKER_PREFIX =
        Regex("""^\s*([^:.!?]{1,$MAX_SPEAKER_LENGTH}?)\s*:\s+(.+)$""", RegexOption.DOT_MATCHES_ALL)

    fun parse(line: String): SpeakerLine {
        val match = SPEAKER_PREFIX.find(line) ?: return SpeakerLine(speaker = null, text = line.trim())
        val (speaker, text) = match.destructured
        return SpeakerLine(speaker.trim(), text.trim())
    }

    fun parseAll(lines: List<String>): List<SpeakerLine> = lines.map(::parse)
}

package com.paulchibamba.margin.feature.read.note

import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.domain.model.Note
import kotlin.math.roundToInt

object NoteHtml {
    private const val TITLE_TAG = "<h3>"
    private const val MAX_CHANNEL = 255

    fun bodyOf(note: Note, chapterLabel: String): String =
        "<div class=\"chapter\">${escape(chapterLabel)}</div>${titleOf(note)}${note.html}"

    fun documentOf(body: String, palette: SurfacePalette): String = """
        <!DOCTYPE html><html><head><meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>${styleFor(palette)}</style></head>
        <body>$body</body></html>
    """.trimIndent()

    fun escape(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    private fun titleOf(note: Note): String =
        if (note.html.trimStart().startsWith(TITLE_TAG)) "" else "<h1>${escape(note.section)}</h1>"

    private fun Color.css(): String {
        val channels = listOf(red, green, blue).map { (it * MAX_CHANNEL).roundToInt() }
        return "rgba(${channels.joinToString(",")},$alpha)"
    }

    private fun styleFor(palette: SurfacePalette) = """
        @font-face{font-family:Newsreader;src:url('file:///android_res/font/newsreader_regular.ttf');font-weight:400}
        @font-face{font-family:Newsreader;src:url('file:///android_res/font/newsreader_semibold.ttf');
          font-weight:600 700}
        @font-face{font-family:Geist;src:url('file:///android_res/font/geist_semibold.ttf');font-weight:600}
        @font-face{font-family:Geist;src:url('file:///android_res/font/geist_bold.ttf');font-weight:700}
        @font-face{font-family:GeistMono;src:url('file:///android_res/font/geist_mono_regular.ttf')}
        html,body{margin:0;background:${palette.card.css()};color:${palette.text.css()}}
        body{padding:18px 24px 40px;font:400 18px/1.6 Newsreader,serif;overflow-wrap:break-word;text-align:left}
        .chapter{font:600 12px Geist,sans-serif;color:${palette.faintText.css()};margin:0 0 14px}
        h1,.chapter+h3{font:700 24px/1.2 Geist,sans-serif;letter-spacing:-.02em;margin:0 0 14px}
        h3{font:700 19px/1.3 Geist,sans-serif;margin:22px 0 10px}
        p{margin:0 0 14px}
        ul{margin:0 0 14px;padding-left:22px}
        li{margin:0 0 6px}
        code{font:15px GeistMono,monospace;border-radius:4px;padding:0 4px;
          background:${palette.text.copy(alpha = 0.06f).css()}}
        pre{font:13px/1.5 GeistMono,monospace;padding:12px;border-radius:10px;white-space:pre-wrap;
          background:${palette.codeBackground.css()};color:${MarginColors.CodeText.css()}}
        pre code{background:none;padding:0;font:inherit;color:inherit}
        figure{margin:6px 0 18px}
        img{display:block;width:100%;height:auto;border-radius:14px;background:${MarginColors.White.css()}}
    """.trimIndent()
}

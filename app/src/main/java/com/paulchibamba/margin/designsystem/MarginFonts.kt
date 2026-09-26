package com.paulchibamba.margin.designsystem

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.paulchibamba.margin.R

object MarginFonts {
    val Geist = FontFamily(
        Font(R.font.geist_regular, FontWeight.Normal),
        Font(R.font.geist_medium, FontWeight.Medium),
        Font(R.font.geist_semibold, FontWeight.SemiBold),
        Font(R.font.geist_bold, FontWeight.Bold),
        Font(R.font.geist_extrabold, FontWeight.ExtraBold),
    )

    val GeistMono = FontFamily(
        Font(R.font.geist_mono_regular, FontWeight.Normal),
        Font(R.font.geist_mono_medium, FontWeight.Medium),
        Font(R.font.geist_mono_semibold, FontWeight.SemiBold),
    )

    val Newsreader = FontFamily(
        Font(R.font.newsreader_regular, FontWeight.Normal),
        Font(R.font.newsreader_medium, FontWeight.Medium),
        Font(R.font.newsreader_semibold, FontWeight.SemiBold),
    )
}

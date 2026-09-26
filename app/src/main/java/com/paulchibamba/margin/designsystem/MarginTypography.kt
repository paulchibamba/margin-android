package com.paulchibamba.margin.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

object MarginTypography {
    val display = geist(FontWeight.ExtraBold, 88.sp, lineHeight = 1.em, letterSpacing = (-0.05).em)
    val splashWordmark = geist(FontWeight.ExtraBold, 34.sp, letterSpacing = (-0.04).em)
    val postHeadline = geist(FontWeight.ExtraBold, 40.sp, lineHeight = 1.02.em, letterSpacing = (-0.035).em)
    val postTitle = geist(FontWeight.ExtraBold, 32.sp, lineHeight = 1.05.em, letterSpacing = (-0.03).em)
    val screenTitle = geist(FontWeight.ExtraBold, 26.sp, lineHeight = 1.1.em, letterSpacing = (-0.025).em)
    val quizQuestion = geist(FontWeight.Bold, 20.sp, lineHeight = 1.25.em, letterSpacing = (-0.015).em)
    val wordmark = geist(FontWeight.ExtraBold, 17.sp, letterSpacing = (-0.04).em)
    val conceptTitle = geist(FontWeight.Bold, 16.sp, lineHeight = 1.25.em, letterSpacing = (-0.01).em)

    val body = geist(FontWeight.Normal, 17.sp, lineHeight = 1.5.em)
    val bodySmall = geist(FontWeight.Normal, 15.sp, lineHeight = 1.5.em)
    val callout = geist(FontWeight.SemiBold, 15.sp, lineHeight = 1.4.em)
    val button = geist(FontWeight.Bold, 15.sp)
    val streakChip = geist(FontWeight.Bold, 14.sp)
    val option = geist(FontWeight.Medium, 14.sp)
    val caption = geist(FontWeight.Medium, 13.sp)
    val label = geist(FontWeight.Medium, 12.sp)
    val navLabel = geist(FontWeight.Medium, 12.sp)
    val navLabelActive = geist(FontWeight.Bold, 12.sp)
    val meta = geist(FontWeight.Medium, 11.5.sp)
    val pill = geist(FontWeight.SemiBold, 11.5.sp)
    val railLabel = geist(FontWeight.SemiBold, 11.sp)

    val mono = style(MarginFonts.GeistMono, FontWeight.Medium, 12.sp)
    val code = style(MarginFonts.GeistMono, FontWeight.Medium, 12.sp, lineHeight = 1.6.em)
    val monoLarge = style(MarginFonts.GeistMono, FontWeight.SemiBold, 19.sp)

    val bookText = style(MarginFonts.Newsreader, FontWeight.Normal, 18.sp, lineHeight = 1.6.em)

    private fun geist(
        weight: FontWeight,
        size: TextUnit,
        lineHeight: TextUnit = TextUnit.Unspecified,
        letterSpacing: TextUnit = TextUnit.Unspecified,
    ) = style(MarginFonts.Geist, weight, size, lineHeight, letterSpacing)

    private fun style(
        family: FontFamily,
        weight: FontWeight,
        size: TextUnit,
        lineHeight: TextUnit = TextUnit.Unspecified,
        letterSpacing: TextUnit = TextUnit.Unspecified,
    ) = TextStyle(
        fontFamily = family,
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )
}

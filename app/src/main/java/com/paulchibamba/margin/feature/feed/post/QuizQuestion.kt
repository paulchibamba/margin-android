package com.paulchibamba.margin.feature.feed.post

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography

val TEST_POST_TOP = 16.dp
val TEST_POST_SPACING = 10.dp

@Composable
fun QuizQuestion(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, style = MarginTypography.quizQuestion, color = LocalSkin.current.content)
}

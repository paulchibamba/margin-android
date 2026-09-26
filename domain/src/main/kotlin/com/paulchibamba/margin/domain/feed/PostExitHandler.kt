package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.signals.EngagementCalculator
import com.paulchibamba.margin.domain.signals.ExpectedReadTime
import com.paulchibamba.margin.domain.signals.GradeMapper
import com.paulchibamba.margin.domain.signals.PostExit
import java.time.Instant

class PostExitHandler(
    private val engagement: EngagementCalculator,
    private val gradeMapper: GradeMapper,
    scheduler: FsrsScheduler,
) {
    private val grader = CardGrader(scheduler)

    fun apply(state: FeedState, post: Post, exit: PostExit, now: Instant): ExitOutcome {
        val score = engagement.score(post.content, exit)
        val afterEngagement = if (exit.isMarkedLess) state else withAffinityAfter(state, post, score)
        val progress = state.progressOf(post.conceptId)
        val card = progress.card
        val grade = gradeFor(post, exit)
        if (card == null || grade == null) return ExitOutcome(afterEngagement, score, null, reviewLogEntry = null)
        val graded = grader.grade(card, post, grade, now, exit.dwell)
        val nextProgress = progressAfterGrade(progress.copy(card = graded.card), grade)
        return ExitOutcome(afterEngagement.withProgress(post.conceptId, nextProgress), score, grade, graded.logEntry)
    }

    private fun withAffinityAfter(state: FeedState, post: Post, score: Double): FeedState =
        state.copy(affinity = state.affinity.afterEngagement(post.format, score))

    private fun gradeFor(post: Post, exit: PostExit): Rating? =
        if (post.role == PostRole.TEST) gradeMapper.gradeFor(exit, ExpectedReadTime.of(post.content)) else null

    private fun progressAfterGrade(progress: ConceptProgress, grade: Rating): ConceptProgress {
        val foundTheWayBack = grade != Rating.AGAIN && progress.confidence == Confidence.LOST
        return progress.copy(
            confidence = if (foundTheWayBack) null else progress.confidence,
            isLostGraded = false,
        )
    }
}

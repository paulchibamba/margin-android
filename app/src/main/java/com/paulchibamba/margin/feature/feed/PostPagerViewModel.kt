package com.paulchibamba.margin.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.designsystem.SkinRotation
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.signals.ExpectedReadTime
import com.paulchibamba.margin.domain.signals.GradeMapper
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.tracking.InteractionKind
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.feature.feed.post.TestResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

abstract class PostPagerViewModel(
    protected val useCases: FeedUseCases,
    random: Random,
    private val clock: Clock,
    private val attention: PostAttention,
) : ViewModel() {

    private val skinRotation = SkinRotation(random)
    private val gradeMapper = GradeMapper()
    protected val state = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = state.asStateFlow()

    init {
        attention.onFeedOpened()
        viewModelScope.launch {
            useCases.observeStreak().collect { streak -> state.update { it.copy(streak = streak.currentStreak) } }
        }
        viewModelScope.launch {
            useCases.observeBookCovers().collect { covers ->
                state.update { it.copy(coverPaths = covers.mapValues { (_, cover) -> cover.imagePath }) }
            }
        }
    }

    open fun onPageEntered(index: Int) {
        noteRewardShown(index)
        state.update { it.copy(currentIndex = index, nudge = it.nudgeOn(index)) }
        updatePage(index) { it.copy(enteredAt = clock.now()) }
        settle(index)
        loadIntervals(index)
    }

    open fun onPageLeft(index: Int, dwell: Duration, isEngaged: Boolean = false) {
        val page = state.value.pages.getOrNull(index) ?: return
        if (page.isExitRecorded) return
        updatePage(index) { it.copy(isExitRecorded = true) }
        val exit = PostExit(
            dwell = dwell,
            isEngaged = isEngaged || page.isEngaged || page.answer != null || page.viewState.isEngagedByAction,
            answer = page.answer?.outcome,
            isMarkedLess = page.viewState.isMarkedLess,
            timeToAnswer = page.answer?.timeToAnswer,
        )
        viewModelScope.launch { useCases.recordExit(page.item.post, exit) }
    }

    fun onRespond(index: Int, response: TestResponse) {
        val page = state.value.pages.getOrNull(index)?.takeIf { it.answer == null } ?: return
        val answer = answerOf(page, response) ?: return
        updatePage(index) { it.copy(answer = answer) }
        attention.onAnswer(page.item.post.id, answer.outcome.isCorrect, answer.timeToAnswer, answer.rating)
        showWhenSeenAgain(index, page, answer.rating)
    }

    fun onEngaged(index: Int) {
        updatePage(index) { it.copy(isEngaged = true) }
    }

    fun onAction(index: Int, action: PostAction) {
        val page = state.value.pages.getOrNull(index) ?: return
        if (!page.viewState.canChoose(action)) return
        updatePage(index) { it.copy(viewState = it.viewState.afterChoosing(action)) }
        attention.onAction(page.item.post.id, action)
        page.item.post.rewardKind?.let { kind -> attention.onReward(page.item.post.id, kind, action.name.lowercase()) }
        viewModelScope.launch {
            val nudge = useCases.applyAction(page.item.post, action).nudge ?: return@launch
            state.update { it.copy(nudge = FeedNudge.of(index, nudge, page.context.conceptTitle)) }
        }
    }

    fun onReadSource(index: Int) = onAction(index, PostAction.READ)

    fun onInteraction(index: Int, kind: InteractionKind) {
        val page = state.value.pages.getOrNull(index) ?: return
        attention.onInteraction(page.item.post.id, kind)
    }

    fun onFeedShown(isShown: Boolean) = attention.onFeedShown(isShown)

    fun onScrolling(isScrolling: Boolean) = attention.onScrolling(isScrolling)

    fun onMore() {
        state.update { it.copy(sheetPageIndex = it.currentIndex.takeIf { index -> index < it.pages.size }) }
    }

    fun onSheetDismiss() {
        state.update { it.copy(sheetPageIndex = null) }
    }

    fun onNudgeDismiss() {
        state.update { it.copy(nudge = null) }
    }

    protected open fun settle(index: Int) = noteAttention(index)

    private fun noteRewardShown(index: Int) {
        val page = state.value.pages.getOrNull(index)?.takeIf { it.enteredAt == null } ?: return
        page.item.post.rewardKind?.let { kind -> attention.onReward(page.item.post.id, kind, REWARD_SHOWN) }
    }

    protected fun noteAttention(index: Int) {
        val page = state.value.pages.getOrNull(index)
        if (page == null) attention.onLeftPosts() else attention.onSettled(page.visitAt(index))
    }

    private fun answerOf(page: FeedPage, response: TestResponse): TestAnswer? {
        val content = page.item.post.content
        val outcome = response.outcomeFor(content) ?: return null
        val timeToAnswer = timeOnPage(page)
        val rating = gradeMapper.gradeFor(outcome, timeToAnswer, ExpectedReadTime.of(content))
        return TestAnswer(response, outcome, timeToAnswer, rating)
    }

    private fun timeOnPage(page: FeedPage): Duration {
        val enteredAt = page.enteredAt ?: return Duration.ZERO
        return JavaDuration.between(enteredAt, clock.now()).toKotlinDuration()
    }

    private fun showWhenSeenAgain(index: Int, page: FeedPage, rating: Rating) {
        viewModelScope.launch {
            val intervals = page.intervals.ifEmpty { useCases.previewIntervals(page.item.post) }
            val interval = intervals[rating] ?: return@launch
            state.update { it.copy(nudge = FeedNudge.seeAgain(index, interval, page.context)) }
        }
    }

    private fun loadIntervals(index: Int) {
        val page = state.value.pages.getOrNull(index)?.takeIf { it.isTest && it.answer == null } ?: return
        viewModelScope.launch {
            val intervals = useCases.previewIntervals(page.item.post)
            updatePage(index) { it.copy(intervals = intervals) }
        }
    }

    protected suspend fun pageOf(item: FeedItem, previous: FeedPage?, dropIndex: Int? = null): FeedPage {
        val context = useCases.describe(item.post)
        val skin = skinRotation.next(previous?.skin, useCases.feedTone())
        return FeedPage(item, context, skin, dropIndex = dropIndex)
    }

    protected fun updatePage(index: Int, change: (FeedPage) -> FeedPage) {
        state.update { current ->
            val pages = current.pages.mapIndexed { pageIndex, page -> if (pageIndex == index) change(page) else page }
            current.copy(pages = pages)
        }
    }

    private companion object {
        const val REWARD_SHOWN = "shown"
    }
}

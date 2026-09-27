package com.paulchibamba.margin.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.designsystem.SkinRotation
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.signals.ExpectedReadTime
import com.paulchibamba.margin.domain.signals.GradeMapper
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.feature.feed.post.TestResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val useCases: FeedUseCases,
    random: Random,
    private val clock: Clock,
) : ViewModel() {

    private val skinRotation = SkinRotation(random)
    private val gradeMapper = GradeMapper()
    private val pageLoading = Mutex()
    private val state = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = state.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.observeStreak().collect { streak -> state.update { it.copy(streak = streak.currentStreak) } }
        }
        loadPagesAhead()
    }

    fun onPageEntered(index: Int) {
        state.update { it.copy(currentIndex = index, nudge = it.nudgeOn(index)) }
        updatePage(index) { it.copy(enteredAt = clock.now()) }
        loadIntervals(index)
        loadPagesAhead()
    }

    fun onPageLeft(index: Int, dwell: Duration, isEngaged: Boolean = false) {
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
        showWhenSeenAgain(index, page, answer.rating)
    }

    fun onEngaged(index: Int) {
        updatePage(index) { it.copy(isEngaged = true) }
    }

    fun onAction(index: Int, action: PostAction) {
        val page = state.value.pages.getOrNull(index) ?: return
        if (!page.viewState.canChoose(action)) return
        updatePage(index) { it.copy(viewState = it.viewState.afterChoosing(action)) }
        viewModelScope.launch {
            val nudge = useCases.applyAction(page.item.post, action).nudge ?: return@launch
            state.update { it.copy(nudge = FeedNudge.of(index, nudge, page.context.conceptTitle)) }
        }
    }

    fun onReadSource(index: Int) = onAction(index, PostAction.READ)

    fun onMore() {
        state.update { it.copy(sheetPageIndex = it.currentIndex.takeIf { index -> index < it.pages.size }) }
    }

    fun onSheetDismiss() {
        state.update { it.copy(sheetPageIndex = null) }
    }

    fun onNudgeDismiss() {
        state.update { it.copy(nudge = null) }
    }

    fun onCaughtUpShown() {
        viewModelScope.launch {
            pageLoading.withLock { if (state.value.isCaughtUp) appendNext() }
            loadPagesAhead()
        }
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

    private fun loadPagesAhead() {
        viewModelScope.launch {
            pageLoading.withLock { while (needsPageAhead()) appendNext() }
        }
    }

    private fun needsPageAhead(): Boolean =
        with(state.value) { !isCaughtUp && pages.size <= currentIndex + PAGES_AHEAD }

    private suspend fun appendNext() {
        when (val result = useCases.nextPost()) {
            is FeedResult.Next -> appendPage(result.item)
            FeedResult.CaughtUp -> {
                val caughtUp = useCases.caughtUp()
                state.update { it.copy(caughtUp = caughtUp) }
            }
        }
    }

    private suspend fun appendPage(item: FeedItem) {
        val context = useCases.describe(item.post)
        val skin = skinRotation.next(state.value.pages.lastOrNull()?.skin)
        state.update { it.copy(pages = it.pages + FeedPage(item, context, skin), caughtUp = null) }
    }

    private fun updatePage(index: Int, change: (FeedPage) -> FeedPage) {
        state.update { current ->
            val pages = current.pages.mapIndexed { pageIndex, page -> if (pageIndex == index) change(page) else page }
            current.copy(pages = pages)
        }
    }

    private companion object {
        const val PAGES_AHEAD = 1
    }
}

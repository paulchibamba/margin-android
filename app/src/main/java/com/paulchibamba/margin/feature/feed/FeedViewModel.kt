package com.paulchibamba.margin.feature.feed

import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.feature.drop.DropUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
class FeedViewModel @Inject constructor(
    useCases: FeedUseCases,
    private val drops: DropUseCases,
    random: Random,
    clock: Clock,
    attention: PostAttention,
) : PostPagerViewModel(useCases, random, clock, attention) {

    private val pageLoading = Mutex()

    init {
        viewModelScope.launch {
            drops.observeToday().collect { status -> state.update { it.copy(dropStatus = status) } }
        }
        viewModelScope.launch { drops.prepareToday() }
        loadPagesAhead()
    }

    override fun onPageEntered(index: Int) {
        super.onPageEntered(index)
        loadPagesAhead()
    }

    fun onCaughtUpShown() = retryWhenCaughtUp()

    fun onClockChanged() = retryWhenCaughtUp()

    private fun retryWhenCaughtUp() {
        viewModelScope.launch {
            pageLoading.withLock { if (state.value.isCaughtUp) appendNext() }
            loadPagesAhead()
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
        val page = pageOf(item, previous = state.value.pages.lastOrNull())
        state.update { it.copy(pages = it.pages + page, caughtUp = null) }
    }

    private companion object {
        const val PAGES_AHEAD = 1
    }
}

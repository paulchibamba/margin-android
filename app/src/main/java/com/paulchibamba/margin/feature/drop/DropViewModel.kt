package com.paulchibamba.margin.feature.drop

import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.drop.DropPage
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.feature.feed.FeedPage
import com.paulchibamba.margin.feature.feed.FeedUseCases
import com.paulchibamba.margin.feature.feed.PostPagerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DropViewModel @Inject constructor(
    useCases: FeedUseCases,
    private val drops: DropUseCases,
    random: Random,
    clock: Clock,
    attention: PostAttention,
) : PostPagerViewModel(useCases, random, clock, attention) {

    init {
        viewModelScope.launch { open() }
    }

    override fun onPageEntered(index: Int) {
        super.onPageEntered(index)
        if (state.value.isDropCompletionPage(index)) showCompletion()
    }

    override fun onPageLeft(index: Int, dwell: Duration, isEngaged: Boolean) {
        val dropIndex = state.value.pages.getOrNull(index)?.takeUnless { it.isExitRecorded }?.dropIndex
        super.onPageLeft(index, dwell, isEngaged)
        if (dropIndex != null) viewModelScope.launch { drops.leave(dropIndex) }
    }

    fun onContinue() {
        viewModelScope.launch { drops.continueIntoFeed() }
    }

    fun onDismiss() {
        viewModelScope.launch { drops.dismiss() }
    }

    override fun settle(index: Int) {
        val page = state.value.pages.getOrNull(index)
        val dropIndex = page?.dropIndex
        if (page == null || dropIndex == null || page.isRecordedInDrop) return super.settle(index)
        updatePage(index) { it.copy(isRecordedInDrop = true) }
        viewModelScope.launch {
            drops.enter(dropIndex, page.item)?.let { recorded -> updatePage(index) { it.copy(item = recorded) } }
            noteAttention(index)
        }
    }

    private suspend fun open() {
        val session = drops.open()
        val pages = session?.pages.orEmpty().fold(emptyList<FeedPage>()) { built, page -> built + pageOf(page, built) }
        state.update { it.copy(pages = pages, drop = DropRun(size = session?.size ?: 0)) }
    }

    private suspend fun pageOf(page: DropPage, built: List<FeedPage>): FeedPage =
        pageOf(page.item, previous = built.lastOrNull(), dropIndex = page.index)

    private fun showCompletion() {
        if (state.value.drop?.completion != null) return
        viewModelScope.launch {
            val completion = drops.completion()
            state.update { it.copy(drop = it.drop?.copy(completion = completion)) }
        }
    }
}

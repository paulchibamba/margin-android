package com.paulchibamba.margin.feature.read.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadRule
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.tracking.NoteAttention
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import com.paulchibamba.margin.domain.tracking.NoteVisit
import com.paulchibamba.margin.domain.tracking.ScrollPosition
import com.paulchibamba.margin.domain.usecase.MarkNoteRead
import com.paulchibamba.margin.domain.usecase.NoteReading
import com.paulchibamba.margin.domain.usecase.ObserveNote
import com.paulchibamba.margin.domain.usecase.RememberLastNote
import com.paulchibamba.margin.feature.celebration.CelebrationTrigger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

private const val NOTE_ID_KEY = "noteId"
private const val FROM_POST_KEY = "fromPost"
private const val VIA_KEY = "via"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeNote: ObserveNote,
    private val markNoteRead: MarkNoteRead,
    private val rememberLastNote: RememberLastNote,
    private val celebrations: CelebrationTrigger,
    private val clock: Clock,
    private val attention: NoteAttention,
) : ViewModel() {

    private val readRule = ReadRule()
    private val fromPost = savedStateHandle.get<String>(FROM_POST_KEY)?.let(::PostId)
    private val currentNote = MutableStateFlow(NoteId(checkNotNull(savedStateHandle.get<String>(NOTE_ID_KEY))))
    private val isReadRuleMet = MutableStateFlow(false)
    private var openedAt: Instant = clock.now()
    private var openedVia = viaOf(savedStateHandle.get<String>(VIA_KEY))

    private val reading: StateFlow<NoteReading?> = currentNote.flatMapLatest(observeNote::invoke)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val uiState: StateFlow<NoteUiState> = combine(reading.filterNotNull(), isReadRuleMet) { reading, isMet ->
        NoteUiState.of(reading, isMet, fromPost)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, NoteUiState(fromPost = fromPost))

    init {
        viewModelScope.launch {
            reading.filterNotNull().map { it.note }.distinctUntilChangedBy(Note::id).collectLatest(::onOpened)
        }
    }

    fun onNext() {
        val current = reading.value ?: return
        viewModelScope.launch {
            attention.onClosed(isMarkedRead = markIfRead(current))
            current.next?.let { next -> moveTo(next, NoteOpenVia.NEXT) }
        }
    }

    fun onPrevious() {
        val previous = reading.value?.previous ?: return
        attention.onClosed(isMarkedRead = false)
        moveTo(previous, NoteOpenVia.PREVIOUS)
    }

    fun onShown(isShown: Boolean) = attention.onShown(isShown)

    fun onScrolled(position: ScrollPosition) = attention.onScrolled(position)

    fun onZoomedIn() = attention.onZoomedIn()

    override fun onCleared() {
        attention.onClosed(isMarkedRead = false)
    }

    private fun moveTo(note: NoteId, via: NoteOpenVia) {
        isReadRuleMet.value = false
        openedVia = via
        currentNote.value = note
    }

    private suspend fun onOpened(note: Note) {
        attention.onOpened(NoteVisit(note.id, note.wordCount, openedVia, note.hasImages))
        openedAt = clock.now()
        isReadRuleMet.value = false
        if (fromPost == null) rememberLastNote(note.id)
        delay(readRule.dwellNeeded(note.wordCount))
        isReadRuleMet.value = true
    }

    private suspend fun markIfRead(current: NoteReading): Boolean {
        if (current.isRead || !readRule.isRead(dwellSinceOpened(), current.note.wordCount)) return false
        withContext(NonCancellable) {
            celebrations.onStreakSignal(markNoteRead(current.note.id).isStreakExtended)
        }
        return true
    }

    private fun viaOf(name: String?): NoteOpenVia =
        NoteOpenVia.entries.firstOrNull { it.name == name } ?: NoteOpenVia.CHAPTER

    private fun dwellSinceOpened(): Duration = JavaDuration.between(openedAt, clock.now()).toKotlinDuration()
}

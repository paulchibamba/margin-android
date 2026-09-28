package com.paulchibamba.margin.feature.read.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadRule
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.usecase.MarkNoteRead
import com.paulchibamba.margin.domain.usecase.NoteReading
import com.paulchibamba.margin.domain.usecase.ObserveNote
import com.paulchibamba.margin.domain.usecase.RememberLastNote
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeNote: ObserveNote,
    private val markNoteRead: MarkNoteRead,
    private val rememberLastNote: RememberLastNote,
    private val clock: Clock,
) : ViewModel() {

    private val readRule = ReadRule()
    private val fromPost = savedStateHandle.get<String>(FROM_POST_KEY)?.let(::PostId)
    private val currentNote = MutableStateFlow(NoteId(checkNotNull(savedStateHandle.get<String>(NOTE_ID_KEY))))
    private val isReadRuleMet = MutableStateFlow(false)
    private var openedAt: Instant = clock.now()

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
            markIfRead(current)
            current.next?.let(::moveTo)
        }
    }

    fun onPrevious() {
        reading.value?.previous?.let(::moveTo)
    }

    private fun moveTo(note: NoteId) {
        isReadRuleMet.value = false
        currentNote.value = note
    }

    private suspend fun onOpened(note: Note) {
        openedAt = clock.now()
        isReadRuleMet.value = false
        if (fromPost == null) rememberLastNote(note.id)
        delay(readRule.dwellNeeded(note.wordCount))
        isReadRuleMet.value = true
    }

    private suspend fun markIfRead(current: NoteReading) {
        if (current.isRead || !readRule.isRead(dwellSinceOpened(), current.note.wordCount)) return
        withContext(NonCancellable) { markNoteRead(current.note.id) }
    }

    private fun dwellSinceOpened(): Duration = JavaDuration.between(openedAt, clock.now()).toKotlinDuration()
}

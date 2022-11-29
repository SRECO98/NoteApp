package com.example.noteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.use_case.NoteUseCases
import com.example.noteapp.feature_note.domain.util.NoteOrder
import com.example.noteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel(){

    //this will be the state that contains the values our UI will observe.
    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    //reference to the last deleted note.
    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null //whenever we call fun getNotes() we want to cancel old coroutine that observes database

    init { //initially loading some notes with default order
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    //this fun will be triggered from UI by user.
    fun onEvent(event: NotesEvent){
        when(event){
            is NotesEvent.Order -> {
                if(
                    state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    recentlyDeletedNote = event.note
                    noteUseCases.deleteNoteUseCase(event.note)
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNoteUseCase(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null  //so if we call this part of fun multiple times we won't insert the same note over and over again because its null.
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy( //copy takes the same value but can change something inside it, and everything else is the same.
                    isOrderSectionVisisble = !state.value.isOrderSectionVisisble
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder){
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotesUseCase(noteOrder = noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }
}
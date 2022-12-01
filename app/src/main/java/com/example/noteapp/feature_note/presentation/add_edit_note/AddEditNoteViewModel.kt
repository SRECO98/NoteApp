package com.example.noteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_note.domain.model.InvalidNoteException
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.use_case.NoteUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditNoteViewModel @Inject constructor (
    private val noteUseCases: NoteUseCases,
    //this below contains navigation arguments so we dont even need to pass that nav. arg. to viewmodel so its enough to get id of clicked note.
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _noteTitle = mutableStateOf(NoteTextFieldState(
        hint = "Enter title..."
    ))
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(NoteTextFieldState(
        hint = "Enter some content..."
    ))
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb()) //toArgb transfer type to Int
    val noteColor: State<Int> = _noteColor

    //one time event
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null // we need to know id for next note that we will add in DB

    init {
        savedStateHandle.get<Int>("noteId")?.let {noteId: Int ->
            if (noteId != -1) { //default we use when we click on floating button
                viewModelScope.launch {
                    noteUseCases.getNoteUseCase(noteId)?.also { note ->  //if not == to null we are also getting all info of that note from DB

                        currentNoteId = note.id

                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )

                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )

                        _noteColor.value = note.color

                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent){
        when(event){

            is AddEditNoteEvent.EnteredTitle -> {  //checking event
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value //changing value in state for title text field.
                )
            }

            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.EnteredContent -> {  //checking event
                _noteContent.value = noteContent.value.copy(
                    text = event.value //changing value in state for title text field.
                )
            }

            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteContent.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }

            is AddEditNoteEvent.SaveNote -> { //saving note
                viewModelScope.launch {
                    try{
                        noteUseCases.addNoteUseCase(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    }catch (e: InvalidNoteException){
                        _eventFlow.emit(
                            UiEvent.showSnackbar(
                                message = e.message ?: "Couldn't save note."
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent{
        data class showSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
    }
}
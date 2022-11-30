package com.example.noteapp.feature_note.domain.use_case

data class NoteUseCases( //this class will be ejected in viewmodel, because we will make our code cleaner if we call only one class and not every use case one by one.
    val getNotesUseCase: GetNotesUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val addNoteUseCase: AddNoteUseCase,
)

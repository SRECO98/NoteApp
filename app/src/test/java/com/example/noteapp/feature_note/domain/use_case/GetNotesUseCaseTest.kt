package com.example.noteapp.feature_note.domain.use_case

import com.example.noteapp.feature_note.data.repository.FakeNoteRepository
import com.example.noteapp.feature_note.domain.model.Note
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

class GetNotesUseCaseTest{
    private lateinit var getNotesUseCase: GetNotesUseCase
    private lateinit var fakeNoteRepository: FakeNoteRepository

    @Before
    fun setUp(){
        fakeNoteRepository = FakeNoteRepository()
        getNotesUseCase = GetNotesUseCase(fakeNoteRepository)

        val notesToInsert = mutableListOf<Note>()
        ('a'..'z').forEachIndexed{ index, c ->
            notesToInsert.add(
                Note(
                    title = c.toString(),
                    content = c.toString(),
                    timestamp = index.toLong(), //index of value in array 'a' to 'z'.
                    color = index,
                )
            )
        }
        notesToInsert.shuffle() // mixing the list so we have a random order :D
        runBlocking {
            notesToInsert.forEach{note ->
                fakeNoteRepository.insertNote(note) // this is sus fun so we need to call it in runBlocking.
            }
        }
    }


}
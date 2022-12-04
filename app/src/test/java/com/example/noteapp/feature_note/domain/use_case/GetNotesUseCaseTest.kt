package com.example.noteapp.feature_note.domain.use_case

import com.example.noteapp.feature_note.data.repository.FakeNoteRepository
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.util.NoteOrder
import com.example.noteapp.feature_note.domain.util.OrderType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

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

    @Test
    fun `Order notes by title ascending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Title(OrderType.Ascending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].title).isLessThan(notes[i+1].title)
        }
    }

    @Test
    fun `Order notes by title descending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Title(OrderType.Descending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].title).isGreaterThan(notes[i+1].title)
        }
    }

    @Test
    fun `Order notes by date ascending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Date(OrderType.Ascending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].timestamp).isLessThan(notes[i+1].timestamp)
        }
    }

    @Test
    fun `Order notes by date descending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Date(OrderType.Descending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].timestamp).isGreaterThan(notes[i+1].timestamp)
        }
    }

    @Test
    fun `Order notes by color ascending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Color(OrderType.Ascending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].color).isLessThan(notes[i+1].color)
        }
    }

    @Test
    fun `Order notes by color descending, correct order`() = runBlocking{
        //we use first here cause its returning a flow and not a list, so we need only first emission
        val notes = getNotesUseCase(NoteOrder.Color(OrderType.Descending)).first() //getting list of notes

        for(i in 0..notes.size - 2){
            assertThat(notes[i].color).isGreaterThan(notes[i+1].color)
        }
    }


}
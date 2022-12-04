package com.example.noteapp.feature_note.domain.use_case

import com.example.noteapp.feature_note.data.repository.FakeNoteRepository
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.util.NoteOrder
import com.example.noteapp.feature_note.domain.util.OrderType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AddNoteUseCaseTest{
    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var getNotesUseCase: GetNotesUseCase

    @Before
    fun setUp(){
        fakeNoteRepository = FakeNoteRepository()
        addNoteUseCase = AddNoteUseCase(fakeNoteRepository)
        getNotesUseCase = GetNotesUseCase(fakeNoteRepository)
    }

    @Test
    fun `inserting one note correct way`(){
        val content = "You have a breakfast at 8AM."
        val noteToInsert = Note(
            title = "Alarm",
            content = content,
            timestamp = 100,
            color = 100,
        )

        runBlockingTest {
            addNoteUseCase(noteToInsert)
            val note = getNotesUseCase(NoteOrder.Title(OrderType.Ascending)).first()

            assertThat(note.get(0).title == "Alarm" &&
                        note.get(0).content == content &&
                        note.get(0).timestamp == (100).toLong() &&
                        note.get(0).color == 100
            ).isTrue()
        }
    }

    @Test
    fun `inserting one note with empty title, throw exception`() {
        val content = "You have a breakfast at 8AM."
        val exceptionMessage = "The title of the note can't be empty."
        val noteToInsert = Note(
            title = "",
            content = content,
            timestamp = 100,
            color = 100,
        )

        runBlockingTest {
            var string: String? = null
            try{
                addNoteUseCase(noteToInsert)
            }catch (e: Exception){
                string = e.message.toString()
            }
            assertThat(string).isEqualTo(exceptionMessage)
        }
    }

    @Test
    fun `inserting one note with empty content, throw exception`(){
        val exceptionMessage = "The content of the note can't be empty."
        val noteToInsert = Note(
            title = "Alarm",
            content = "",
            timestamp = 100,
            color = 100,
        )

        runBlockingTest {
            var string: String? = null
            try{
                addNoteUseCase(noteToInsert)
            }catch (e: Exception){
                string = e.message.toString()
            }
            assertThat(string).isEqualTo(exceptionMessage)
        }
    }

}
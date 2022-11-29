package com.example.noteapp.feature_note.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id") //:id == id we passed in fun
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE) //If we call this fun with existing id in base it will just update the value
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}
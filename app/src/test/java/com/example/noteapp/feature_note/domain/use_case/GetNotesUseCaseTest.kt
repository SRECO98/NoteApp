package com.example.noteapp.feature_note.domain.use_case

import org.junit.Assert.*
import org.junit.Before

class GetNotesUseCaseTest{
    private lateinit var getNotesUseCase: GetNotesUseCase

    @Before
    fun setUp(){
        getNotesUseCase = GetNotesUseCase()
    }
}
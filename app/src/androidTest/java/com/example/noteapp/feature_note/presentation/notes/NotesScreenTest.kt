package com.example.noteapp.feature_note.presentation.notes

import com.example.noteapp.di.AppModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*

@HiltAndroidTest
@UninstallModules(AppModule::class)//hey dagger dont use this module.
class NotesScreenTest{

}
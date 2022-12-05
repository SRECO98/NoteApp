package com.example.noteapp.feature_note.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.core.util.TestTags
import com.example.noteapp.di.AppModule
import com.example.noteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.example.noteapp.feature_note.presentation.notes.NotesScreen
import com.example.noteapp.feature_note.presentation.util.Screen
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)//hey dagger dont use this module.
class NotesEndToEndTest {

    //we can with this rule call inject fun before test case so we make sure it is correctly injected.
    @get:Rule(order = 0) //smaller number will run before (ORDER).
    val hiltRule = HiltAndroidRule(this)

    //we need compose rule to make sure we run MainAcitivity and not some random activity.
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp(){
        hiltRule.inject()
        composeRule.setContent { //this is copied from MainActivity
            NoteAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route,
                ){

                    composable(route = Screen.NotesScreen.route){
                        NotesScreen(navController = navController)
                    }

                    composable(
                        route = Screen.AddEditNoteScreen.route +
                                "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ){
                                type = NavType.IntType
                                defaultValue = -1
                            },

                            navArgument(
                                name = "noteColor"
                            ){
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        )
                    ){
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards(){
        composeRule
            .onNodeWithContentDescription("Add note")
            .performClick()
        //now we are on the next screen after clicking floating button and also we have to add TestTags
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .performTextInput("test_title") //this text will be entered in text field for title!
        composeRule
            .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
            .performTextInput("test_content")

        composeRule
            .onNodeWithContentDescription("Save note")
            .performClick()
        //Now we are again at first NotesScreen after saving EditedNote:
        composeRule
            .onNodeWithText("test_title")
            .assertIsDisplayed() //checking is the note in our list.

        composeRule
            .onNodeWithText("test_title")
            .performClick()
        //we are again at EditNoteScreen of "test_title":
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .assertTextEquals("test_title") //checking is the text in this note in field Title: test_title
        composeRule
            .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
            .assertTextEquals("test_content")
        composeRule
            .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
            .performTextInput("2")
        composeRule
            .onNodeWithContentDescription("Save note")
            .performClick() //saving again changed Note
        //We are at NotesScreen again:
        composeRule
            .onNodeWithText("test_title2")
            .assertIsDisplayed() //cheking can we find a changed Note in the list
    }

    @Test
    fun saveNewNotes_orderByTitleDescending(){
        for(i in 1..3){ //making three new Notes
            composeRule
                .onNodeWithContentDescription("Add note")
                .performClick()
            //now we are on the next screen after clicking floating button and also we have to add TestTags
            composeRule
                .onNodeWithTag(TestTags.TITLE_TEXT_FIELD)
                .performTextInput(i.toString()) //this text will be entered in text field for title!
            composeRule
                .onNodeWithTag(TestTags.CONTENT_TEXT_FIELD)
                .performTextInput(i.toString())
            composeRule
                .onNodeWithContentDescription("Save note")
                .performClick()
            //Now we are again at first NotesScreen after saving EditedNote:
        }
        //checking are new Notes on the Screen
        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Sort")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Title") //Added Modifier.semantics{} in DefaultRadioButton
            .performClick() //ordering by Title
        composeRule
            .onNodeWithContentDescription("Descending")
            .performClick() //ordering Descending

        composeRule
            .onAllNodesWithTag(TestTags.NOTE_ITEM)[0] //this return 3 Notes, but we need only first one.
            .assertTextContains("3") //If the first one contains "3" after sorting by title descending the sort is working.
        composeRule
            .onAllNodesWithTag(TestTags.NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule
            .onAllNodesWithTag(TestTags.NOTE_ITEM)[2]
            .assertTextContains("1")
    }

}
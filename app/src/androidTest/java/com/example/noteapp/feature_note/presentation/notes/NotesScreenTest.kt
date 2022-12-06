package com.example.noteapp.feature_note.presentation.notes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.core.util.TestTags
import com.example.noteapp.di.AppModule
import com.example.noteapp.feature_note.presentation.MainActivity
import com.example.noteapp.feature_note.presentation.util.Screen
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)//hey dagger dont use this module.
class NotesScreenTest{

    //we can with this rule call inject fun before test case so we make sure it is correctly injected.
    @get:Rule(order = 0) //smaller number will run before (ORDER).
    val hiltRule = HiltAndroidRule(this)

    //we need compose rule to make sure we run MainAcitivity and not some random activity.
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    //we can now simulate some clicks on specific icon
    @Before
    fun setUp(){
        hiltRule.inject()
        composeRule.setContent {
            val navController = rememberNavController()
            NoteAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ){
                    composable(route = Screen.NotesScreen.route){
                        NotesScreen(navController = navController)
                    }
                }
            }
        }
    }

    @Test
    fun clickToggleOrderSection_isVisible_isNotVisible(){
        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertDoesNotExist()//at start order radio buttons shouldnt be visible.
        composeRule.onNodeWithContentDescription("Sort").performClick()//finding icon and performing a click on it.
        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertIsDisplayed()//check if this is displayed on our screen(visible)
        composeRule.onNodeWithContentDescription("Sort").performClick()//closing options for ordering.
        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertDoesNotExist()//checking is button visible
    }


}
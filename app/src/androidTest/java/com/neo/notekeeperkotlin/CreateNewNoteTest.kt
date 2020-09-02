package com.neo.notekeeperkotlin

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

// imports added manually for automated ui tests
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import com.neo.notekeeperkotlin.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard


// test class that runs with androidJunit4Runner
@RunWith(AndroidJUnit4::class)
class CreateNewNoteTest{

    // implements the Activity test rule(specifies activity associated with test) and @JVMField is must for kotlin UI tests
    @Rule @JvmField
    val noteListActivity = ActivityTestRule(NoteListActivity::class.java)

    @Test
    fun createNewNote(){
        // course to be selected from the spinner
        val course = DataManager.courses["android_async"]

        val noteTitle = "Test note Title"
        val noteText = "This is the body of our test note"

        // performs action on View withId matched(ViewMatcher) specified
        onView(withId(R.id.fab)).perform(click())

        // general way of dealing with adapterView but in this case we call on Data after since it's a spinner
        // does action on View if data type of content to select is a courseInfo obj and choose the course passed
        onView(withId(R.id.spinnerCourses)).perform(click())
        onData(allOf(instanceOf(CourseInfo::class.java), equalTo(course))).perform(click())

        onView(withId(R.id.textNoteTitle)).perform(typeText(noteTitle))
        onView(withId(R.id.textNoteText)).perform(typeText(noteText), closeSoftKeyboard())

        // press the back Button
        Espresso.pressBack()

        // test to check if new note wa created and makes sure it matches what was passed
        val newNote = DataManager.notes.last()
        assertEquals(course, newNote.course)
        assertEquals(noteTitle, newNote.title)
        assertEquals(noteText, newNote.text)
    }



}
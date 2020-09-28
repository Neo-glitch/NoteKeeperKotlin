package com.neo.notekeeperkotlin

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

// imports added manually for automated ui tests
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import androidx.test.espresso.assertion.ViewAssertions.*
import org.junit.Test
import androidx.test.espresso.contrib.*


@RunWith(AndroidJUnit4::class)
class NavigationTest{

    @Rule @JvmField
    val itemsActivity = ActivityTestRule(IItemsActivity::class.java)


    // verify the behaviour of RV after user makes selection in nav drawer # checks that rv behaves well
    // even after makes selection onnav_view
    @Test
    fun selectNoteAfterNavigationDrawerChanged(){
        // opens the nav drawer and select the courses icon from navView
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_courses))

        val coursePosition = 0;

        // selects the item at pos passed
        onView(withId(R.id.listItems)).perform(RecyclerViewActions
            .actionOnItemAtPosition<CourseRecyclerAdapter.ViewHolder>(coursePosition, click()))


        // open nav drawer and select the note icon from the navView
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes))

        val notePosition = 0
        onView(withId(R.id.listItems)).perform(RecyclerViewActions
            .actionOnItemAtPosition<NoteRecyclerAdapter.ViewHolder>(notePosition, click()))


        // to verify that correct note is displayed in noteActivity
        val note = DataManager.notes[notePosition]
        onView(withId(R.id.spinnerCourses)).check(matches(withSpinnerText(note.course?.title)))
        onView(withId(R.id.textNoteTitle)).check(matches(withText(note.title)))
        onView(withId(R.id.textNoteText)).check(matches(withText(note.text)))

    }




}
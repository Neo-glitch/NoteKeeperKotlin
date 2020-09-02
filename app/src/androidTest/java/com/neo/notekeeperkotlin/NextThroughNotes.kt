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
import org.hamcrest.Matchers
import androidx.test.espresso.assertion.ViewAssertions.*


@RunWith(AndroidJUnit4::class)
class NextThroughNotes{

    // implements the Activity test rule(specifies activity associated with test) and @JVMField is must for kotlin UI tests
    @Rule @JvmField
    val noteListActivity = ActivityTestRule(NoteListActivity::class.java)

    // test that tests the next menu icon works effectively
    @Test
    fun nextThroughNotes(){
        // match on item must hav content of noteInfo and if so we click on the first note in the adapterView
        onData(allOf(instanceOf(NoteInfo::class.java), equalTo(DataManager.notes[0]))).perform(click())

        // iters through dm notes list & 0..x means range(0, x)
        for(index in 0..DataManager.notes.lastIndex){
            val note = DataManager.notes[index]

            // checks if text in View is same as value in match
            onView(withId(R.id.spinnerCourses)).check(
                matches(withSpinnerText(note.course?.title))
            )

            onView(withId(R.id.textNoteTitle)).check(
                matches(withText(note.title))
            )
            onView(withId(R.id.textNoteText)).check(
                matches(withText(note.text))
            )

            // to help avoid pressing next menuItem when at lastIndex
            if(index != DataManager.notes.lastIndex){
                // does action if view is id passed and the ViewisEnabled
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click())
            }
        }

        // checks if view is not enabled
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())))

//        Espresso.pressBack()


    }



}
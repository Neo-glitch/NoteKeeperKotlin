package com.neo.notekeeperkotlin

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


/**
 * displays a selected  note
 */
class MainActivity : AppCompatActivity() {
    private val tag = this::class.simpleName

    private var notePosition = POSITION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // array adapter(spinner) and list is from hashMap.values.toList()
        val adapterCourses = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            DataManager.courses.values.toList()
        )
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses

        // gets pos from bundle else get it from the intent if bundle null
        notePosition = savedInstanceState?.getInt(NOTE_POSITION) ?: intent.getIntExtra(
            NOTE_POSITION,
            POSITION_NOT_SET
        )

        if (notePosition != POSITION_NOT_SET) {
            displayNote()
        } else {     // create new Note by adding empty note to dataStore and set notePos to that index
            createNewNote()
        }

        Log.d(tag, "onCreate: ")
    }

    private fun createNewNote() {
        DataManager.notes.add(NoteInfo())
        notePosition = DataManager.notes.lastIndex
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(NOTE_POSITION, notePosition)
        super.onSaveInstanceState(outState)
    }

    /**
     * populate the views in this element with note members at this position retrieved
     */
    private fun displayNote() {
        // makes sure not pos is valid and in list of dm notes list
        if(notePosition > DataManager.notes.lastIndex){
            showMessage("Note not found")
            Log.e(tag, "Invalid note Position: $notePosition, max Valid position: ${DataManager.notes.lastIndex}")
            return
        }

        Log.i(tag, "Displaying note for pos: $notePosition")
        val note = DataManager.notes[notePosition]
        textNoteTitle.setText(note.title)
        textNoteText.setText(note.text)

        // gets coursePos of the notes Course in DataManger class courses list and set the spinner to that course, since list DM = list spinner
        val coursePosition = DataManager.courses.values.indexOf(note.course)
        spinnerCourses.setSelection(coursePosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // switch case in kotlin that returns value of case met and else is like default stat
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_next -> {
                if(notePosition < DataManager.notes.lastIndex){
                    moveNext()              // moves to next note in list and populate the spinner with assoc course title
                } else{
                    val message = "No more Notes"
                    showMessage(message)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMessage(message: String) =
        Snackbar.make(textNoteTitle, message, Snackbar.LENGTH_LONG).show()


    private fun moveNext() {
        ++notePosition
        displayNote()
        invalidateOptionsMenu()        // calls onPrepareOptionsMenu
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (notePosition >= DataManager.notes.lastIndex) {    // if at last index of notesList
            val menuItem = menu?.findItem(R.id.action_next)
            if (menuItem != null) {
                menuItem.icon = getDrawable(R.drawable.ic_block_white_24dp)
                menuItem.isEnabled = false
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        saveNote()
        super.onPause()
        Log.d(tag, "onPause: ")
    }

    /**
     * simulate saving a note to db, but done on memory
     */
    private fun saveNote() {
        val note = DataManager.notes[notePosition]
        note.title = textNoteTitle.text.toString()
        note.text = textNoteText.text.toString()
        // gets the course selected from spinner and type cast as CourseInfo obj
        note.course = spinnerCourses.selectedItem as CourseInfo

    }
}

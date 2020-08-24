package com.neo.notekeeperkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_note_list.*

/**
 * displays list of notes
 */
class NoteListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            // intent creation
            val activityIntent = Intent(this, MainActivity::class.java)
            startActivity(activityIntent)
            overridePendingTransition(0, 0)
        }

        listNotes.adapter = ArrayAdapter(this,
        android.R.layout.simple_list_item_1,
        DataManager.notes)

        // does interface method implementation here where needed and parent, view e.t.c is param needed by interface func
        listNotes.setOnItemClickListener{parent, view, position, id ->
            val activityIntent = Intent(this, MainActivity::class.java)
            activityIntent.putExtra(NOTE_POSITION, position)            // sends pos of item clicked in list of notes
            startActivity(activityIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        // cast the adapter from list as an arrayAdapter and call notifyDataSetChanged
        (listNotes.adapter as ArrayAdapter<NoteInfo>).notifyDataSetChanged()

    }
}
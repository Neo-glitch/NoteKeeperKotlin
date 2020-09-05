package com.neo.notekeeperkotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.app_bar_items.*
import kotlinx.android.synthetic.main.content_items.*
import kotlinx.android.synthetic.main.nav_header_items.*

class ItemsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // lazy prop i.e ain't init until it's been used (e.g in onCreate()).
    private val noteLayoutManager by lazy {LinearLayoutManager(this)}
    private val noteRecyclerAdapter by lazy {NoteRecyclerAdapter(this, DataManager.notes)}

    private val courseLayoutManager by lazy { GridLayoutManager(this, 2) }
    private val courseRecyclerAdapter by lazy { CourseRecyclerAdapter(this, DataManager.courses.values.toList()) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {view ->
            startActivity(Intent(this, NoteActivity::class.java))
        }


        displayNotes()


        // sets up toggle for the drawerLayout and navView
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()        // tells toggle the state of the drawer

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun displayNotes() {
        // sets up the rv
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = noteRecyclerAdapter

        // makes sure the item in nav View is selected when launch app
        nav_view.menu.findItem(R.id.nav_notes).isChecked = true
    }

    private fun displayCourses() {
        // sets up the rv
        listItems.layoutManager = courseLayoutManager
        listItems.adapter = courseRecyclerAdapter

        // makes sure the item in nav View is selected when launch app
        nav_view.menu.findItem(R.id.nav_courses).isChecked = true
    }

    override fun onResume() {
        super.onResume()
        listItems.adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_notes -> {
                displayNotes()
            }
            R.id.nav_courses -> {
                displayCourses()
            }
            R.id.nav_share -> {
                handleSelection("share")
            }
            R.id.nav_send -> {
                handleSelection("send")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun handleSelection(message: String) {
        Snackbar.make(textView, message, Snackbar.LENGTH_LONG).show()
    }
}

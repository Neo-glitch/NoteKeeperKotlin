package com.neo.notekeeperkotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_note_list.*
import kotlinx.android.synthetic.main.content_note_list.*
import kotlinx.android.synthetic.main.layout_settings_toolbar.*


/**
 * shows list of notes along and houses the navDrawer
 */
class IItemsActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    NoteRecyclerAdapter.OnNoteSelectedListener,
    IItems,
    ChangePhotoDialog.OnPhotoReceivedListener {

    private val TAG = "ItemsActivity"
    private var accountFragment: AccountFragment? = null
    private var settingsFragment: SettingsFragment? = null


    private val noteLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val noteRecyclerAdapter by lazy {
        val adapter = NoteRecyclerAdapter(this, DataManager.loadNotes())
        adapter.setOnSelectedListener(this)
        adapter
    }

    private val courseLayoutManager by lazy {
        GridLayoutManager(this, 2)
    }
    private val courseRecyclerAdapter by lazy {
        CourseRecyclerAdapter(this, DataManager.courses.values.toList())
    }

    private val recentlyViewedNoteRecyclerAdapter by lazy {
        val adapter = NoteRecyclerAdapter(this, viewModel.recentlyViewedNotes)
        adapter.setOnSelectedListener(this)
        adapter
    }

    // ref to our ViewModel class
    private val viewModel by lazy { ViewModelProviders.of(this)[ItemsActivityViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(this, NoteActivity::class.java))
        }

        // true only when sys cleans up activity thereby destroying ViewModel
        if (savedInstanceState != null && viewModel.isNewlyCreated) {
            viewModel.restoreState(savedInstanceState)
        }
        viewModel.isNewlyCreated = false

        handleDisplaySelection(viewModel.navDrawerDisplaySelection)

        // sets toggle for DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        registerNotificationChannel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // saves in bundle using the ViewModel method
        viewModel.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // creates the notif channel
            val channel = NotificationChannel(
                ReminderNotification.REMINDER_CHANNEL,
                "NoteReminders",    // what user will see when comm with notif channel in appSettings
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)

        }
    }

    private fun displayNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = noteRecyclerAdapter

        // marks this menu item in the nav_View
        nav_view.menu.findItem(R.id.nav_notes).isChecked = true
    }

    private fun displayCourses() {
        listItems.layoutManager = courseLayoutManager
        listItems.adapter = courseRecyclerAdapter

        nav_view.menu.findItem(R.id.nav_courses).isChecked = true
    }

    private fun displayRecentlyViewedNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = recentlyViewedNoteRecyclerAdapter

        nav_view.menu.findItem(R.id.nav_recent_notes).isChecked = true
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

        correctSettingsToolbarVisibility()
    }


    // fun hides settings toolbar if settings fragment is visible when it's called, else it show it
    private fun correctSettingsToolbarVisibility() {
        if (settingsFragment != null) {
            if (settingsFragment!!.isVisible) {
                showSettingsAppBar()
            } else {
                hideSettingsAppBar()
            }
            return
        }
        hideSettingsAppBar()
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
        return when (item.itemId) {
            R.id.action_settings -> {
                inflateAccountFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun inflateAccountFragment(){
        if(accountFragment == null){
            accountFragment = AccountFragment()
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.account_container, accountFragment!!, FRAGMENT_ACCOUNT)
        transaction.addToBackStack(FRAGMENT_ACCOUNT)
        transaction.commit()
    }

    private fun inflateSettingsFragment() {
        // inflates the settings fragment into the container
        Log.d(TAG, "inflating settings fragment")
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment()
        }
        var transaction = fragmentManager.beginTransaction()
            .replace(R.id.settings_container, settingsFragment, FRAGMENT_SETTINGS)
        transaction.addToBackStack(FRAGMENT_SETTINGS)
        transaction.commit()
    }

    override fun showSettingsAppBar() {
        settings_app_bar.visibility = View.VISIBLE
    }

    override fun hideSettingsAppBar() {
        settings_app_bar.visibility = View.GONE
    }

    override fun setImageUri(imageUri: Uri?) {
        accountFragment!!.setImageUri(imageUri)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_notes,
            R.id.nav_courses,
            R.id.nav_recent_notes -> {
                handleDisplaySelection(item.itemId)
                // sets the prop in the ViewModel to this
                viewModel.navDrawerDisplaySelection = item.itemId
            }
            R.id.nav_share -> {
                handleSelection("Don't you think you've shared enough")
            }
            R.id.nav_send -> {
                handleSelection("Send")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    /**
     * used to get list to show based on based on item passed as arg
     */
    fun handleDisplaySelection(itemId: Int) {
        when (itemId) {
            R.id.nav_notes -> {
                displayNotes()
            }
            R.id.nav_courses -> {
                displayCourses()
            }
            R.id.nav_recent_notes -> {
                displayRecentlyViewedNotes()
            }
        }
    }

    override fun onNoteSelected(note: NoteInfo) {
        viewModel.addToRecentlyViewedNotes(note)
    }


    private fun handleSelection(message: String) {
        Snackbar.make(listItems, message, Snackbar.LENGTH_LONG).show()
    }




}

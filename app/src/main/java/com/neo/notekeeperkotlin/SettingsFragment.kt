package com.neo.notekeeperkotlin

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


/**
 * fragment for showing settings using preference framework
 */
class SettingsFragment: PreferenceFragment() {

    private var TAG = "SettingsFragment"
    private var iIItems: IItems? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // load pref fragment from an xml resource
        addPreferencesFromResource(R.xml.pref_main)
    }

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fix for transparent background when showing the pref_main layout
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))

        iIItems!!.showSettingsAppBar()

        // changes the toolbar in itemActivity to the prev hidden toolbar since pref frag has no toolbar
        // and gets the actionBar after it has been set for other jobs
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(activity.findViewById(R.id.settings_toolbar) as Toolbar)
        val actionBar = activity.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "SettingsFragment"
        setHasOptionsMenu(true)    // enables backArrow in swapped settings toolbar to work

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        // gets the menu item with that id
        val menuItem: MenuItem = menu!!.findItem(R.id.action_settings)
        menuItem.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            // back arrow is clicked
            iIItems!!.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            iIItems = (context as IItems)
        }catch (e: Exception){
            Log.d(TAG, "onAttach: error: ${e.printStackTrace()}")
        }
    }

}
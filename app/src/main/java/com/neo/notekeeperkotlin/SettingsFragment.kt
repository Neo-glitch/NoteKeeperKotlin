package com.neo.notekeeperkotlin

import android.content.Context
import android.os.Bundle
import android.preference.Preference
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
class SettingsFragment : PreferenceFragment(),
    Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private var TAG = "SettingsFragment"
    private var iIItems: IItems? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load pref fragment from an xml resources
        addPreferencesFromResource(R.xml.pref_main)

        // set the Preference CLick listener
        val accountPreference: Preference = findPreference(getString(R.string.key_account_settings))
        accountPreference.setOnPreferenceClickListener { onPreferenceClick(it) }        // 'onPreferenceClick' is the listener method below while 'it' is the account pref

        // sets the Preference Change Listener
        val galleryNamePreference: Preference = findPreference(getString(R.string.key_gallery_name))
        galleryNamePreference.setOnPreferenceChangeListener(this)

        val uploadWifiPreference: Preference? =
            preferenceManager.findPreference(getString(R.string.key_upload_over_wifi))
        uploadWifiPreference?.setOnPreferenceChangeListener(this)

        val notificationsNewMessagePreference: Preference =
            findPreference(getString(R.string.key_notifications_new_message))
        notificationsNewMessagePreference.setOnPreferenceChangeListener(this)

        val notificationsRingtonePreference: Preference =
            findPreference(getString(R.string.key_notifications_new_message_ringtone))
        notificationsRingtonePreference.setOnPreferenceChangeListener(this)

        val vibratePreference: Preference = findPreference(getString(R.string.key_vibrate))
        vibratePreference.setOnPreferenceChangeListener(this)

        val backupFrequencyPreference: Preference =
            findPreference(getString(R.string.key_backup_frequency))
        backupFrequencyPreference.setOnPreferenceChangeListener(this)
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

    override fun onPreferenceClick(preference: Preference?): Boolean {
        // listens for click to a preference obj
        if (preference!!.key.equals(getString(R.string.key_account_settings))) {
            iIItems!!.inflateAccountFragment()
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        // listens for changes to a preference obj
        Log.d(TAG, "onPreferenceChange: change detected")
        when (newValue) {
            getString(R.string.key_gallery_name) -> updatePreferenceSuccess(getString(R.string.key_gallery_name))
            getString(R.string.key_upload_over_wifi) -> updatePreferenceSuccess(getString(R.string.key_upload_over_wifi))
            getString(R.string.key_notifications_new_message) -> updatePreferenceSuccess(getString(R.string.key_notifications_new_message))
            getString(R.string.key_notifications_new_message_ringtone) -> updatePreferenceSuccess(getString(R.string.key_notifications_new_message_ringtone))
            getString(R.string.key_vibrate) -> updatePreferenceSuccess(getString(R.string.key_vibrate))
            getString(R.string.key_backup_frequency) -> updatePreferenceSuccess(getString(R.string.key_backup_frequency))
        }

        // Update the state of the preference with the new value
        return true
    }

    fun updatePreferenceSuccess(key: String?){

        // If this was a real application we would send the updates to server here
        uploadPreferencesToServer()
        Log.d(TAG, "updatePreferenceSuccess: update success. key: key")

    }

    private fun uploadPreferencesToServer(){
        // Code for uploading updated preferences to server but not implemented in dummy app
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        // gets the menu item with that id
        val menuItem: MenuItem = menu!!.findItem(R.id.action_settings)
        menuItem.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            // back arrow is clicked
            iIItems!!.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            iIItems = (context as IItems)
        } catch (e: Exception) {
            Log.d(TAG, "onAttach: error: ${e.printStackTrace()}")
        }
    }


}
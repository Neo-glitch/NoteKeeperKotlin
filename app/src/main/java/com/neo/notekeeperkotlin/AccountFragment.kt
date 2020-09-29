package com.neo.notekeeperkotlin

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.input.InputManager
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_account_toolbar.*
import java.util.*

// tells fragment where to find set and get method for helper class
import com.neo.notekeeperkotlin.PreferenceHelper.set
import com.neo.notekeeperkotlin.PreferenceHelper.get


/**
 * fragment housing user details, pops up when account pref in settings fragment is clicked
 */
class AccountFragment : Fragment(),
        View.OnClickListener
{
    private val TAG = "AccountFragment"

    private var selectedImageUri: Uri? = null
    private var permissions: Boolean = false
    private var iItems: IItems? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // formats phone number as user types them in
        input_phone_number.addTextChangedListener(PhoneNumberFormattingTextWatcher(Locale.getDefault().country))

        // sets listener to "X" and "checkMark" icon
        initToolbar()
        initWidgetValues()
        initPermissions()
        enablePhotoSelection()
    }

    private fun initPermissions(){
        if (!permissions) {
            verifyPermissions()
        }
    }

    private fun enablePhotoSelection(){
        change_photo.setOnClickListener(this)
        profile_image.setOnClickListener(this)
    }


    fun setProfileImage(url: String?){
        val requestOptions: RequestOptions = RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(profile_image)
    }

    /**
     * passes the imageUri to glide to set the image in the imageView
     */
    fun setImageUri(imageUri: Uri?) {
        if (imageUri.toString() != "") {
            selectedImageUri = imageUri
            printToLog("getImagePath: got the image uri: " + selectedImageUri)

            val requestOptions: RequestOptions = RequestOptions()
                    .placeholder(R.mipmap.ic_launcher_round)

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(selectedImageUri)
                    .into(profile_image)
        }
    }


    private fun inflateChangePhotoDialog(){
        if(permissions){
            val dialog = ChangePhotoDialog()
            val fm = requireActivity().supportFragmentManager
            dialog.show(fm, getString(R.string.dialog_change_photo))
        }
        else{
            verifyPermissions()
        }

    }

    override fun onClick(widget: View?) {
        when(widget?.id){

            R.id.close -> iItems!!.onBackPressed()

            R.id.save -> savePreferences()

            R.id.profile_image -> inflateChangePhotoDialog()

            R.id.change_photo -> inflateChangePhotoDialog()
        }
    }


    // gets the values saved in sharedPref
    private fun initWidgetValues(){

        val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(requireContext())

        // Option 1: Specify the type in the declaration
        val name: String? = prefs[PREFERENCES_NAME]
        input_name.setText(name)

        // Option 2: Specify the type indirectly by setting the default value
        val username = prefs[PREFERENCES_USERNAME, ""]
        input_username.setText(username)

        val email: String? = prefs[PREFERENCES_EMAIL]
        input_email_address.setText(email)

        val phoneNumber: String? = prefs[PREFERENCES_PHONE_NUMBER]
        input_phone_number.setText(phoneNumber)

        val gender: String? = prefs[PREFERENCES_GENDER]
        if(gender.equals("")){
            gender_spinner.setSelection(0)
        }
        else{
            val genderArray = resources.getStringArray(R.array.gender_array)
            val genderIndex: Int = genderArray.indexOf(gender)
            gender_spinner.setSelection(genderIndex)
        }

        val profileImageUrl: String? = prefs[PREFERENCES_PROFILE_IMAGE]
        setProfileImage(profileImageUrl)
    }

    fun savePreferences(){
        requireView().hideKeyboard()

        val prefs: SharedPreferences = PreferenceHelper.defaultPrefs(requireContext())

        // name
        printToLog("saving name: " + input_name.text.toString())

        // this operator overload is possible since set method is an opp overload fun
        prefs[PREFERENCES_NAME] = input_name.text.toString()


        // username
        val username: String = input_username.text.toString().replace(" ", ".")
        printToLog("saving username: " + username)
        prefs[PREFERENCES_USERNAME] = username
        input_username.setText(username) // fix the username being displayed if necessary

        // Phone Number
        val phoneNumber: String = removeNumberFormatting(input_phone_number.text.toString())
        printToLog("saving phone number: " + phoneNumber)
        prefs[PREFERENCES_PHONE_NUMBER] = phoneNumber

        // Email Address
        printToLog("saving email address: " + input_email_address.text.toString())
        prefs[PREFERENCES_EMAIL] = input_email_address.text.toString()

        // Gender
        printToLog("saving gender: " + gender_spinner.selectedItem.toString())
        prefs[PREFERENCES_GENDER] = gender_spinner.selectedItem


        if(selectedImageUri != null){
            prefs[PREFERENCES_PROFILE_IMAGE] = selectedImageUri.toString()
        }

    }

    private fun removeNumberFormatting(number: String): String{
        val regex = Regex("[^0-9]")
        return regex.replace(number, "")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            iItems = (activity as IItems)
        }catch (e: ClassCastException){
            printToLog(e.message)
        }
    }

    fun View.hideKeyboard(){
        Log.d(TAG, "closing keyboard")
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showProgressBar(){
        save.visibility = View.INVISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progress_bar.visibility = View.INVISIBLE
        save.visibility = View.VISIBLE
    }


    private fun initToolbar() {
        close.setOnClickListener(this)
        save.setOnClickListener(this)
    }

    fun verifyPermissions() {
        Log.d(TAG, "verifyPermissions: asking user for permissions.")
        val permissionsArray = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                permissionsArray[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.requireContext(),
                permissionsArray[1]) == PackageManager.PERMISSION_GRANTED){
            permissions = true
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsArray,
                    PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun printToLog(message: String?){
        Log.d(TAG, message)
    }
}
















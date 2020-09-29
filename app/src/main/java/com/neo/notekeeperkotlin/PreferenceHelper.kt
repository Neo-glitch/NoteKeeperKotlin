package com.neo.notekeeperkotlin

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.Editable
import java.lang.UnsupportedOperationException


/**
 * helper singleton class for centralizing all sharedPreferences transaction
 * i.e it will hold a single shared Pref instance that will be available anywhere in this app
 */
object PreferenceHelper {

    fun defaultPrefs(context: Context): SharedPreferences
    // gets the def sharedPreferences for entire app
            = PreferenceManager.getDefaultSharedPreferences(context)


    fun customPrefs(context: Context, name: String): SharedPreferences
    // gets a sharedPreference obj for an activity or any other processes, rarely used
            =context.getSharedPreferences(name, Context.MODE_PRIVATE)


    /**
     * kinda higher order function that acc accepts a fun (with an editor as param)
     * and "this" is of type SharedPref
     */
    inline fun SharedPreferences.edit(function: (SharedPreferences.Editor) -> Unit){
        val editor = this.edit()
        function(editor)      // do the work needed, running the function body
        editor.apply()
    }


    /**
     * fun for setting values to the sharedPreferences
     * SharedPreferences.set means creating a new method for the shared pref class # no need to create a class to extend
     * SharedPreferences and add this set method
     */
    operator fun SharedPreferences.set(key: String, value: Any){
        when(value){
            is Editable -> {
                if(!value.toString().equals("")){
                    edit{
                        // it is the editor passed to func in higher order func
                        it.putString(key, value.toString())}
                }
            }
            is String ->{
                if (!value.equals("")){
                    edit{it.putString(key, value)}
                }
            }
            is Int -> edit{it.putInt(key, value)}
            is Boolean -> edit{it.putBoolean(key, value)}
            is Float -> edit{it.putFloat(key, value)}
            is Long -> edit{it.putLong(key, value)}
            else -> throw UnsupportedOperationException("Unsupported operation")
        }
    }


    /**
     * retrieves value in the sharedPreference obj and "T" is like a generic i.e anything
     * and reified modifier is used to access an object type in this fun
     */
    operator inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            // based on class or type that default Value belongs to the case is fired
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Unsupported Operation")
        }
    }


    /*
        WANT MORE INFORMATION?
        1) https://kotlinlang.org/docs/reference/lambdas.html
        2) https://kotlinlang.org/docs/reference/keyword-reference.html
        3) https://kotlinlang.org/docs/reference/extensions.html
        4) https://kotlinlang.org/docs/reference/object-declarations.html
        5) https://kotlinlang.org/docs/reference/operator-overloading.html

     */
}
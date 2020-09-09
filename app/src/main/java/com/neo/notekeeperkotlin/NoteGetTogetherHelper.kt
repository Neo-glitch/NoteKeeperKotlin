package com.neo.notekeeperkotlin

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent


/**
 * LifeCycle Observer class that will run the Sim LocationManager class and the Sim Messaging Manager class
 * on lifecycle events
 */
class NoteGetTogetherHelper(context : Context, val lifecycle: Lifecycle) : LifecycleObserver{

    init{
        // reg this class as an observer of the lifecycle obj passed
        lifecycle.addObserver(this)
    }

    val TAG = this::class.simpleName

    // current lat and longitude
    var currentLat = 0.0
    var currentLon = 0.0

    val locationManager = PseudoLocationManager(context){lat, lon ->
        currentLat = lat
        currentLon = lon
        Log.d(TAG, "Location callback lat: $currentLat lon: $currentLon")
    }

    val msgManager = PseudoMessagingManager(context)

    // set the null since msgConnection will be null until callback lambda exp is called msgManager.connect() function
    var msgConnection : PseudoMessagingConnection? = null



    fun sendMessage(note: NoteInfo){
        val getTogetherMessage = "$currentLat | $currentLon | ${note.title} | ${note.course?.title}"
        msgConnection?.send(getTogetherMessage)
    }


    // starts the sim location manager service, when onSTart of lifecycle Owner(Activity) is called
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startHandler(){
        Log.d(TAG, "startHandler")
        locationManager.start()


        msgManager.connect { connection ->
            Log.d(TAG, "Connection callback - Lifecycle state: ${lifecycle.currentState}")
            // as long as lifeCycle state is at least started i.e from onStart() down else disconnect conn
            if(lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                msgConnection = connection
            } else{
                connection.disconnect()
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopHandler(){
        Log.d(TAG, "stopHandler")
        locationManager.stop()
        msgConnection?.disconnect()
    }
    
    
}
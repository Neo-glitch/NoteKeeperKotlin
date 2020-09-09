package com.neo.notekeeperkotlin

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log


/**
 * used to simulate sending our location to a backend service along with the note
 * cloud messaging
 */
class PseudoMessagingManager(private val context: Context) {
    private val tag = this::class.simpleName

    private val connectionCallbackMilliseconds = 5000L
    private val postHandler = Handler(Looper.getMainLooper())

    fun connect(connectionCallback: (PseudoMessagingConnection) -> Unit) {
        Log.d(tag, "Initiating connection...")

        // sim delay in doing something, # sim async work, after wait time we get ref to the Messaging connection
        postHandler.postDelayed(
                {
                    Log.d(tag, "Connection established")
                    // lambda exp gets calledBack after delay, and creates a messaging fun instance
                    connectionCallback(PseudoMessagingConnection())
                },
                connectionCallbackMilliseconds)
    }
}

class PseudoMessagingConnection {
    private val tag = this::class.simpleName

    fun send(message: String) {
        Log.d(tag, message)
    }

    fun disconnect() {
        Log.d(tag, "Disconnected")
    }
}
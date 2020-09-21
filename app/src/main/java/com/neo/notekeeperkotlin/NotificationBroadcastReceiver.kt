package com.neo.notekeeperkotlin

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.fonts.SystemFonts


/**
 * Receiver that handles the reply intent from messaging style notification
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        // gets the remote input bundle holding the text
        val bundle = RemoteInput.getResultsFromIntent(intent)

        if(bundle != null){
            val notePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET)
            val text = bundle.getCharSequence(ReminderNotification.KEY_TEXT_REPLY)?.toString()?:""

            // adds to list showing in noteActivity rv
            DataManager.notes[notePosition].comments.add(0,
                // name is null since we want to use name set in the Messaging style
                NoteComment(null, text, System.currentTimeMillis()))

            // calls the notification and entered text to the comments list shown on the notification
            ReminderNotification.notify(
                context,
                DataManager.notes[notePosition],
                notePosition
            )
            bundle.clear()
        }
    }
}

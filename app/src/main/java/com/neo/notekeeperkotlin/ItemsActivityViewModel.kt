package com.neo.notekeeperkotlin

import android.os.Bundle
import androidx.lifecycle.ViewModel


/**
 * NOTE: ViewModel classes doesn't survive activity destruction due to sys cleanUp( for this use onSaveInstanceState)
 */
class ItemsActivityViewModel : ViewModel(){

    // boolean to tell when ViewModel is newly created inorder to avoid doing lengthy data operation like getting notes from DM
    // when the activity is not destroyed due to sys cleanUp
    var isNewlyCreated = true;

    var navDrawerDisplaySelectionName = "com.neo.notekeeperkotlin.ItemActivityViewModel.navDrawerDisplaySelection"
    var recentlyViewedNotesIdsName = "com.neo.notekeeperkotlin.ItemActivityViewModel.recentlyViewedNoteIds"

    // prop to hold id of last nav menu item clicked on
    var navDrawerDisplaySelection = R.id.nav_notes

    //prop to hold an ArrayList, where max = maxRecentlyViewedNotes
    private val maxRecentlyViewedNotes = 5
    val recentlyViewedNotes = ArrayList<NoteInfo>(maxRecentlyViewedNotes)


    // function to manage the recently Viewed notes
    fun addToRecentlyViewedNotes(note: NoteInfo) {
        // Check if selection is already in the list
        val existingIndex = recentlyViewedNotes.indexOf(note)
        if (existingIndex == -1) {
            // it isn't in the list...
            // Add new one to beginning of list and remove any beyond max we want to keep
            recentlyViewedNotes.add(0, note)
            for (index in recentlyViewedNotes.lastIndex downTo maxRecentlyViewedNotes)
                recentlyViewedNotes.removeAt(index)
        } else {
            // it is in the list...
            // Shift the ones above down the list and make it first member of the list
            for (index in (existingIndex - 1) downTo 0)
                recentlyViewedNotes[index + 1] = recentlyViewedNotes[index]
            recentlyViewedNotes[0] = note
        }
    }

    fun saveState(outState: Bundle) {
        outState.putInt(navDrawerDisplaySelectionName, navDrawerDisplaySelection)
        // gets ids of notes stored in param passed
        val noteIds = DataManager.noteIdsAsIntArray(recentlyViewedNotes)
        outState.putIntArray(recentlyViewedNotesIdsName, noteIds)

    }

    fun restoreState(savedInstanceState: Bundle) {
        navDrawerDisplaySelection = savedInstanceState.getInt(navDrawerDisplaySelectionName)
        val notedIds = savedInstanceState.getIntArray(recentlyViewedNotesIdsName)

        // "*" is spread operator and makes array compatible with var length arg list
        val noteList = DataManager.loadNotes(*notedIds!!)
        recentlyViewedNotes.addAll(noteList)

    }
}
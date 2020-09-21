package com.neo.notekeeperkotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService


/**
 * does same work as an adapter class but for a widget
 */
class AppWidgetRemoteViewsFactory(val context: Context): RemoteViewsService.RemoteViewsFactory{


    override fun onCreate() {
        // needed if we needed to int our dataset
    }

    override fun onDataSetChanged() {

    }

    override fun getLoadingView(): RemoteViews? {
        // used to show a dummy View to user before our data is loading
        return null
    }

    override fun getItemId(position: Int): Long {
        // ret id of particular item in our dataset based on pos in the collectionView
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        // where binding of data to remoteViews is done, # onCreateView and onBindViewHolder together
        val rv = RemoteViews(context.packageName, R.layout.item_note_widget)
        rv.setTextViewText(R.id.note_title, DataManager.notes[position].title)

        val extras = Bundle()
        extras.putInt(NOTE_POSITION, position)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        // adds the fillIn intent to finish our pendingIntent Template process
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return rv
    }

    override fun getCount(): Int {
        // similar to getCount
        return DataManager.notes.size
    }

    override fun getViewTypeCount(): Int {
        // ret the number of type of Views that will be returned in this class
        return 1
    }

    override fun onDestroy() {
        // used to clean up any data or cursor that might been used
    }
}
package com.neo.notekeeperkotlin

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.TaskStackBuilder

/**
 * Implementation of App Widget functionality.
 */
class NoteKeeperAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent?) {
        // handles any broadcast sent to the widget
        val action = intent?.action
        if(action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
            // refresh all widgets
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NoteKeeperAppWidget::class.java)
            // updates only the note_list
            manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.notes_list)
        }

        super.onReceive(context, intent)
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object, to show in the app Widget
            val views = RemoteViews(context.packageName, R.layout.note_keeper_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            // set the adapter up and associate with listView in remoteView
            views.setRemoteAdapter(
                R.id.notes_list,
                Intent(context, AppWidgetRemoteViewsService::class.java)
            )

            // sets up a pending intent with BackStack and then use as template for the listView
            val intent = Intent(context, NoteActivity::class.java)

            val pendingIntent = TaskStackBuilder.create(context)
                // intent to go to and also adds the intent parent and the parents parents... for best backStack exp
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            views.setPendingIntentTemplate(R.id.notes_list, pendingIntent)



            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        /**
         * func sends broadcast to app widget to refresh it data shown in collection
         */
        fun sendRefreshBroadcast(context: Context){
            var intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.component = ComponentName(context, NoteKeeperAppWidget::class.java)
            // sends broadcast to the app widget to refresh
            context.sendBroadcast(intent)
        }
    }


}






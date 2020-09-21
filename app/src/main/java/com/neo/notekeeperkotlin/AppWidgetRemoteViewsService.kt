package com.neo.notekeeperkotlin

import android.content.Intent
import android.widget.RemoteViewsService


/**
 * service provides the AppWidgetRemoteViewsFactoryClass to the remoteViews
 */
class AppWidgetRemoteViewsService : RemoteViewsService() {


    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return AppWidgetRemoteViewsFactory(applicationContext)
    }
}
package com.avcialper.batterytracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.avcialper.batterytracker.R
import com.avcialper.batterytracker.model.Widget
import com.avcialper.batterytracker.ui.MainActivity

class BatteryTrackerWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sharedPreferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val widgets = sharedPreferences.getStringSet("widgetIds", HashSet<String>())
        val ids = HashSet<String>()

        widgets?.let { idList ->
            ids.addAll(idList)
        }

        for (id in appWidgetIds) {
            ids.add(id.toString())
        }

        editor.putStringSet("widgetIds", ids)
        editor.apply()

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

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        if (context != null) {
            val sharedPreferences =
                context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val widgets = sharedPreferences.getStringSet("widgetIds", HashSet<String>())
            val ids = HashSet<String>()
            widgets?.let { idList ->
                ids.addAll(idList)
            }

            appWidgetIds?.let { idList ->
                for (id in idList) {
                    ids.remove(id.toString())
                }
            }

            editor.putStringSet("widgetIds", ids)
            editor.apply()

        }
        super.onDeleted(context, appWidgetIds)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.battery_tracker_widget)
    // Construct the RemoteViews object
    if (Widget.serviceIsOpen) {

        views.setTextColor(R.id.widgetBatteryLevel, Widget.color)
        views.setTextViewText(R.id.widgetBatteryLevel, "${Widget.batteryLevel}%")
        views.setViewVisibility(R.id.widgetBatteryLevel, View.VISIBLE)
        views.setViewVisibility(R.id.renewIcon, View.GONE)
        views.setViewVisibility(R.id.widgetChargeIcon, Widget.chargeIcon)

    } else {
        views.setViewVisibility(R.id.widgetBatteryLevel, View.GONE)
        views.setViewVisibility(R.id.widgetChargeIcon, View.GONE)
        views.setViewVisibility(R.id.renewIcon, View.VISIBLE)
    }

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        101,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.renewIcon, pendingIntent)
    views.setOnClickPendingIntent(R.id.widgetBatteryLevel, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

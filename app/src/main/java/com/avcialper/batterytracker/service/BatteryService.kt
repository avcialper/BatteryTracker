package com.avcialper.batterytracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.avcialper.batterytracker.R
import com.avcialper.batterytracker.br.BatteryBroadcastReceiver

class BatteryService : Service() {

    private lateinit var batteryBroadcastReceiver: BatteryBroadcastReceiver

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(99, notification)

        batteryBroadcastReceiver = BatteryBroadcastReceiver(null)
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryBroadcastReceiver, filter)

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(batteryBroadcastReceiver)

        // Change widget's visibility
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val views = RemoteViews(this.packageName, R.layout.battery_tracker_widget)
        views.setViewVisibility(R.id.widgetBatteryLevel, View.GONE)
        views.setViewVisibility(R.id.widgetChargeIcon, View.GONE)
        views.setViewVisibility(R.id.renewIcon, View.VISIBLE)

        val ids = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
            .getStringSet("widgetIds", HashSet<String>())

        ids?.let { idList ->
            for (id in idList)
                appWidgetManager.updateAppWidget(id.toInt(), views)
        }

        super.onDestroy()
    }

    private fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(this, "serviceChannelId")
            .setContentTitle("Battery Tracker")
            .setContentText("Batarya durum bilgisi takibi başladı.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "serviceChannelId",
                "Battery Tracker",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return notification.build()
    }
}
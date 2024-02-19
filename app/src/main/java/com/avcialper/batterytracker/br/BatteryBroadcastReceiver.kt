package com.avcialper.batterytracker.br

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import com.avcialper.batterytracker.R
import com.avcialper.batterytracker.model.Battery
import com.avcialper.batterytracker.model.Widget
import com.avcialper.batterytracker.ui.MainViewModel

class BatteryBroadcastReceiver(private val viewModel: MainViewModel?) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {

            if (viewModel != null)
                changeLiveData(viewModel, intent)

            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -10)
            val batteryIsCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -10)

            val color =
                when (batteryLevel) {
                    in 81..100 -> context.getColor(R.color.green)
                    in 51..80 -> context.getColor(R.color.green_yellow)
                    in 21..50 -> context.getColor(R.color.yellow)
                    in 6..20 -> context.getColor(R.color.orange)
                    else -> context.getColor(R.color.red)
                }

            Widget.color = color
            Widget.chargeIcon = if (batteryIsCharging != 0) View.VISIBLE else View.GONE
            Widget.batteryLevel = batteryLevel

            if (Widget.serviceIsOpen) {

                val appWidgetManager = AppWidgetManager.getInstance(context)
                val views = RemoteViews(context.packageName, R.layout.battery_tracker_widget)

                views.setTextViewText(R.id.widgetBatteryLevel, "$batteryLevel%")
                views.setTextColor(R.id.widgetBatteryLevel, color)
                views.setViewVisibility(R.id.widgetBatteryLevel, View.VISIBLE)
                views.setViewVisibility(R.id.renewIcon, View.GONE)
                views.setViewVisibility(R.id.widgetChargeIcon, Widget.chargeIcon)

                val ids = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    .getStringSet("widgetIds", HashSet<String>())

                ids?.let { idList ->
                    for (id in idList)
                        appWidgetManager.updateAppWidget(id.toInt(), views)
                }
            }

        }
    }

    // Update screen data
    private fun changeLiveData(viewModel: MainViewModel, intent: Intent) {

        val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -10)
        val batteryIsCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -10)
        val batteryTemperature =
            intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -10).div(10)
        val batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -10)
        val batteryTechnology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        val batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -10)
        val batteryCycleCount =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -10)
            } else {
                "Bilinmiyor"
            }

        val health = when (batteryHealth) {
            2 -> "İyi"
            3 -> "Aşırı Isınma"
            4 -> "Bitik"
            5 -> "Yüksek Voltaj"
            6 -> "Hata"
            7 -> "Aşırı Soğuk"
            else -> "Bilinmiyor"
        }

        val isCharging = if (batteryIsCharging != 0) "Şarj Oluyor" else "Şarj Olmuyor"

        val battery = Battery(
            batteryLevel,
            isCharging,
            batteryTemperature,
            batteryVoltage,
            batteryTechnology!!,
            health,
            batteryCycleCount.toString()
        )

        viewModel.updateBattery(battery)
    }
}
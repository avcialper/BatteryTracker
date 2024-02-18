package com.avcialper.batterytracker.br

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.view.View
import com.avcialper.batterytracker.R
import com.avcialper.batterytracker.model.Battery
import com.avcialper.batterytracker.model.Widget
import com.avcialper.batterytracker.ui.MainViewModel

class BatteryBroadcastReceiver(private val viewModel: MainViewModel?) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {

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

            val color =
                when (batteryLevel) {
                    in 81..100 -> context.getColor(R.color.green)
                    in 51..80 -> context.getColor(R.color.green_yellow)
                    in 21..50 -> context.getColor(R.color.yellow)
                    in 6..20 -> context.getColor(R.color.orange)
                    else -> context.getColor(R.color.red)
                }

            val battery = Battery(
                batteryLevel,
                isCharging,
                batteryTemperature,
                batteryVoltage,
                batteryTechnology!!,
                health,
                batteryCycleCount.toString()
            )

            viewModel?.updateBattery(battery)

            Widget.color = color
            Widget.chargeIcon = if (batteryIsCharging != 0) View.VISIBLE else View.GONE

            val appWidgetManager = AppWidgetManager.getInstance(context)
            // TODO widget id'lerini al, remoteView değerlerini al ve gerekli güncellemeleri yap
            // TODO şarj yüzdesi rengi, şarj yüzdesi, şarj iconu güncellenecek

        }
    }
}
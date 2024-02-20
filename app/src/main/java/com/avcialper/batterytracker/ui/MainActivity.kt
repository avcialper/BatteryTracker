package com.avcialper.batterytracker.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.avcialper.batterytracker.R
import com.avcialper.batterytracker.br.BatteryBroadcastReceiver
import com.avcialper.batterytracker.databinding.ActivityMainBinding
import com.avcialper.batterytracker.model.Widget
import com.avcialper.batterytracker.service.BatteryService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var batteryBroadcastReceiver: BatteryBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)

        initUI()
        startBatteryReceiver()
        checkServices()
    }

    // To check the current battery status
    private fun startBatteryReceiver() {
        batteryBroadcastReceiver = BatteryBroadcastReceiver(viewModel)
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryBroadcastReceiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(batteryBroadcastReceiver)
        super.onDestroy()
    }

    // Is service on check
    private fun checkServices() {
        val services = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val isOpen = services.getRunningServices(1).isNotEmpty()
        Widget.serviceIsOpen = isOpen

        // Service is closed
        if (!isOpen) {
            val intent = Intent(this, BatteryService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent)
            else
                startService(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        binding.apply {
            viewModel.battery.observe(this@MainActivity) { battery ->
                cfl.setProgress(100 - battery.level)
                batteryLevel.text = "${battery.level}%"
                chargeTV.text = battery.isCharging
                voltageTV.text = "${battery.voltage} V"
                temperatureTV.text = "${battery.temperature} °C"
                technologyTV.text = battery.technology
                healthTV.text = battery.health
                cycleTV.text = battery.cycleCount
            }
        }
    }
}
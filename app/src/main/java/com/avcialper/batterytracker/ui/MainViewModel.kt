package com.avcialper.batterytracker.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avcialper.batterytracker.model.Battery

class MainViewModel : ViewModel() {

    var battery = MutableLiveData<Battery>()

    init {
        val startBattery = Battery()
        battery.value = startBattery
    }

    fun updateBattery(battery: Battery) {
        this.battery.value = battery
    }
}
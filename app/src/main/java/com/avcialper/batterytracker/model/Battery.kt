package com.avcialper.batterytracker.model

import android.graphics.Color

data class Battery(
    var level: Int = 0,
    var isCharging: String = "",
    var temperature: Int = 0,
    var voltage: Int = 0,
    var technology: String = "",
    var health: String = "",
    var cycleCount: String = "",
)

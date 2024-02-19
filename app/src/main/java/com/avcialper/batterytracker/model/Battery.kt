package com.avcialper.batterytracker.model

data class Battery(
    var level: Int = 0,
    var isCharging: String = "",
    var temperature: Int = 0,
    var voltage: Int = 0,
    var technology: String = "",
    var health: String = "",
    var cycleCount: String = "",
)

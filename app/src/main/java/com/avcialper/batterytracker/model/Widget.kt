package com.avcialper.batterytracker.model

import android.view.View

class Widget {
    companion object {
        var batteryLevel: Int = 0
        var serviceIsOpen: Boolean = false
        var chargeIcon: Int = View.GONE
        var color: Int = 0
    }
}
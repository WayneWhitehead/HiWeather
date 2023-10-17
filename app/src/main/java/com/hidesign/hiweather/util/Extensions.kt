package com.hidesign.hiweather.util

import java.math.RoundingMode

object Extensions {
    fun Double.roundToDecimal(): Double {
        val number = this.toBigDecimal().setScale(1, RoundingMode.HALF_UP)
        return number.toDouble()
    }
}
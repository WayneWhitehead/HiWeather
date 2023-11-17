package com.hidesign.hiweather.util

import com.hidesign.hiweather.util.Extensions.roundToDecimal
import org.junit.Assert
import org.junit.Test

class ExtensionsTest {

    @Test
    fun roundToDecimal_success() {
        val double = 123.456789
        val roundedDouble = double.roundToDecimal()

        Assert.assertEquals(123.5, roundedDouble, 0.0)
    }

    @Test
    fun roundToDecimal_negative() {
        val double = -123.456789
        val roundedDouble = double.roundToDecimal()

        Assert.assertEquals(-123.5, roundedDouble, 0.0)
    }

    @Test
    fun roundToDecimal_zero() {
        val double = 0.0
        val roundedDouble = double.roundToDecimal()

        Assert.assertEquals(0.0, roundedDouble, 0.0)
    }
}

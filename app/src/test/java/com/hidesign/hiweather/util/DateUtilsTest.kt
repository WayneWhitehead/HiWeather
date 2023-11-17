package com.hidesign.hiweather.util

import org.junit.Assert
import org.junit.Test

class DateUtilsTest {

    @Test
    fun getHours_positive() {
        val start = 1661564800L // 2023-08-04T12:00:00.000Z
        val end = 1661601200L // 2023-08-04T20:00:00.000Z

        val hours = DateUtils.getHours(start, end)

        Assert.assertEquals("10 HOURS & 6 MINUTES", hours)
    }

    @Test
    fun getHours_negative() {
        val start = 1661601200L // 2023-08-04T20:00:00.000Z
        val end = 1661564800L // 2023-08-04T12:00:00.000Z

        val hours = DateUtils.getHours(start, end)

        Assert.assertEquals("10 HOURS & 6 MINUTES", hours)
    }

    @Test
    fun getDateTime_success() {
        val timeInt = 1661651400L // 2023-08-05T00:00:00.000Z
        val timezone = "UTC"

        val dateTime = DateUtils.getDateTime(DateUtils.RISE_SET_FORMAT, timeInt, timezone)

        Assert.assertEquals("01:50", dateTime)
    }

    @Test
    fun getDayOfWeekText_success() {
        val timeInt = 1661651400L // 2023-08-05T00:00:00.000Z
        val timezone = "UTC"

        val dayOfWeekText = DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, timeInt, timezone)

        Assert.assertEquals("Sunday", dayOfWeekText)
    }

    @Test
    fun getDayOfWeekText_invalidDay() {
        val timeInt = 1661651400L // 2023-08-05T00:00:00.000Z
        val timezone = "UTC"
        val invalidPattern = "invalidPattern"

        val dayOfWeekText = DateUtils.getDayOfWeekText(invalidPattern, timeInt, timezone)

        Assert.assertEquals("Unknown", dayOfWeekText)
    }
}

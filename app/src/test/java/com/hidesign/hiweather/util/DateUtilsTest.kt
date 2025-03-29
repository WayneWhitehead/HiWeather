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
        val timezone = "UTC"
        val daysOfWeek = mapOf(
            1661651400L to "Sunday",    // 2023-08-05T00:00:00.000Z
            1661737800L to "Monday",    // 2023-08-06T00:00:00.000Z
            1661824200L to "Tuesday",   // 2023-08-07T00:00:00.000Z
            1661910600L to "Wednesday", // 2023-08-08T00:00:00.000Z
            1661997000L to "Thursday",  // 2023-08-09T00:00:00.000Z
            1662083400L to "Friday",    // 2023-08-10T00:00:00.000Z
            1662169800L to "Saturday",  // 2023-08-11T00:00:00.000Z
        )

        for ((timeInt, expectedDay) in daysOfWeek) {
            val dayOfWeekText = DateUtils.getDayOfWeekText(DateUtils.DAILY_FORMAT, timeInt, timezone)
            Assert.assertEquals(expectedDay, dayOfWeekText)
        }
    }

    @Test
    fun getDayOfWeekText_invalidPattern() {
        val timeInt = 1661651400L // 2023-08-05T00:00:00.000Z
        val timezone = "UTC"
        val invalidPattern = "invalidPattern"

        val dayOfWeekText = DateUtils.getDayOfWeekText(invalidPattern, timeInt, timezone)

        Assert.assertEquals("Unknown", dayOfWeekText)
    }
}

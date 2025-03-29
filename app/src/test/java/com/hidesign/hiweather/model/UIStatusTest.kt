package com.hidesign.hiweather.model

import com.hidesign.hiweather.data.model.ErrorType
import com.hidesign.hiweather.data.model.UIStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UIStatusTest {

    @Test
    fun testSuccessStatus() {
        val successStatus = UIStatus.Success
        assertTrue(successStatus is UIStatus.Success)
    }

    @Test
    fun testErrorStatus() {
        val errorStatus = UIStatus.Error(ErrorType.WEATHER_ERROR)
        assertTrue(errorStatus is UIStatus.Error)
        assertEquals(ErrorType.WEATHER_ERROR, (errorStatus as UIStatus.Error).type)
    }
}
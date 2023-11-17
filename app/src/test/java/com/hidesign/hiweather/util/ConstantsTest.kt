package com.hidesign.hiweather.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.hidesign.hiweather.R
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class ConstantsTest {

    @InjectMockKs
    val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        every { context.resources.getStringArray(R.array.temperature_units) } returns arrayOf("Celsius", "Fahrenheit", "Kelvin")
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE) } returns mockk()
    }

    @Test
    fun getAPIKey() {
        val key = "weatherKey"
        val expectedValue = "YOUR_API_KEY"

        val bundle = mockk<Bundle>()
        every { bundle.getString(key, "") } returns expectedValue

        val applicationInfo = mockk<ApplicationInfo>()
        applicationInfo.metaData = bundle

        every { context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA) } returns applicationInfo

        val actualValue = Constants.getAPIKey(context, key)
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun getUnit_celsius() {
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.TEMPERATURE_UNIT, 0) } returns 0

        val unit = Constants.getUnit(context)
        assertEquals("metric", unit)
    }

    @Test
    fun getUnit_fahrenheit() {
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.TEMPERATURE_UNIT, 0) } returns 1

        val unit = Constants.getUnit(context)
        assertEquals("imperial", unit)
    }

    @Test
    fun getUnit_kelvin() {
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.TEMPERATURE_UNIT, 0) } returns 2

        val unit = Constants.getUnit(context)
        assertEquals("", unit)
    }

    @Test
    fun getUnit_default() {
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getInt(Constants.TEMPERATURE_UNIT, 0) } returns 3

        val unit = Constants.getUnit(context)
        assertEquals("metric", unit)
    }
}

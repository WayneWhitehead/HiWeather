package com.hidesign.hiweather.util

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.hidesign.hiweather.util.AdUtil.APP_BAR_AD
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class AdUtilTest {

    private val context: Context = mockk(relaxed = true)
    private val adUtilMock: AdUtil = mockk()

    @Before
    fun setup() {
        every { adUtilMock.setupAds(context, APP_BAR_AD) } returns mockk()
        every { MobileAds.initialize(context) } returns Unit
    }

    @Test
    fun setupAds_success() {
        adUtilMock.setupAds(context, APP_BAR_AD)
        verify { adUtilMock.setupAds(context, APP_BAR_AD) }
        verify { MobileAds.initialize(context) }
    }

    @Test
    fun setupAds_failure() {
        every { adUtilMock.setupAds(context, APP_BAR_AD) } throws Exception("Exception")

        try {
            adUtilMock.setupAds(context, APP_BAR_AD)
            Assert.fail("Expected exception")
        } catch (e: Exception) {
            Assert.assertEquals("Exception", e.message)
        }
    }
}
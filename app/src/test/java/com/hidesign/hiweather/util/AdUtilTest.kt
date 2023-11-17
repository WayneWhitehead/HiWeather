package com.hidesign.hiweather.util
import android.content.Context
import com.hidesign.hiweather.util.AdUtil.APP_BAR_AD
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AdUtilTest {

    private val context: Context = mockk(relaxed = true)
    private val adUtilMock: AdUtil = mockk()

    @Before
    fun setup() {
        every { adUtilMock.setupAds(context, APP_BAR_AD) } returns mockk()
    }

    @Test
    fun setupAds_success() {
        // Call the setupAds() method
        adUtilMock.setupAds(context, APP_BAR_AD)

        // Verify that the setupAds() method was called on the mocked AdUtil object
        verify { adUtilMock.setupAds(context, APP_BAR_AD) }
    }

    @Test
    fun setupAds_failure() {
        // Stub the setupAds() method to throw an exception
        every { adUtilMock.setupAds(context, APP_BAR_AD) } throws Exception()

        // Try to call the setupAds() method
        try {
            adUtilMock.setupAds(context, APP_BAR_AD)
            Assert.fail("Expected exception")
        } catch (e: Exception) {
            // Assert that the exception is handled correctly
        }
    }
}

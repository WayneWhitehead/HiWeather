package com.hidesign.hiweather

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hidesign.hiweather.presentation.components.ForecastIconLabel
import com.hidesign.hiweather.presentation.ForecastImageLabel
import com.hidesign.hiweather.presentation.LoadPicture
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposableTests {

    @Rule
    @JvmField
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadPicture() {
        val mockUrl = "https://example.com/image.png"
        composeTestRule.setContent {
            LoadPicture(url = mockUrl, contentDescription = "Image")
        }

        Thread.sleep(1000)

        val image = composeTestRule.onNodeWithContentDescription("Image")
        image.assertIsDisplayed()
    }

    @Test
    fun testForecastImageLabel() {
        val forecastItem = "Precipitation"
        composeTestRule.setContent {
            ForecastImageLabel(forecastItem = forecastItem, image = painterResource(id = R.drawable.rain))
        }

        val image = composeTestRule.onNodeWithContentDescription("$forecastItem Icon")
        image.assertIsDisplayed()

        val text = composeTestRule.onNodeWithText(forecastItem)
        text.assertIsDisplayed()
    }

    @Test
    fun testForecastIconLabel() {
        val forecastItem = "Cloudiness"
        composeTestRule.setContent {
            ForecastIconLabel(forecastItem = forecastItem, icon = Icons.Filled.Cloud)
        }

        val image = composeTestRule.onNodeWithContentDescription("$forecastItem Icon")
        image.assertIsDisplayed()

        val text = composeTestRule.onNodeWithText(forecastItem)
        text.assertIsDisplayed()
    }
}

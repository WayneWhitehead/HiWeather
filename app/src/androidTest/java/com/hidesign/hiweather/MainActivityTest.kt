package com.hidesign.hiweather

import android.Manifest
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainActivityTest {

    private val sharedPreferencesMock = mockkClass(SharedPreferences::class)
    private val sharedPreferencesEditorMock = mockkClass(SharedPreferences.Editor::class)

    @Before
    fun setup() {
        every { sharedPreferencesMock.edit() } returns sharedPreferencesEditorMock
        every { sharedPreferencesEditorMock.apply() } returns Unit
    }

    @Test
    fun testOnCreate() {
        verify { locationPermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)) }
    }

    private val locationPermissionsLauncher: ActivityResultLauncher<Array<String>> =
        mockk(relaxed = true)
}

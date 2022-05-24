package com.hidesign.hiweather.util

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hidesign.hiweather.R

object DialogUtil {
    fun displayInfoDialog(context: Context, title: String = "Air Quality Information", message: String){
        MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialogs_Neutral)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                return@setPositiveButton
            }
            .show()
    }

    fun displayErrorDialog(context: Context, code: Int, message: String) {
        MaterialAlertDialogBuilder(context, R.style.AppTheme_Dialogs_Error)
            .setTitle("Error $code")
            .setMessage(message)
            .setNeutralButton("OK") { _, _ ->
                return@setNeutralButton
            }
            .show()
    }
}
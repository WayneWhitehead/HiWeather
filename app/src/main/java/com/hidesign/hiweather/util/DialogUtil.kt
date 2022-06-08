package com.hidesign.hiweather.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hidesign.hiweather.R

object DialogUtil {

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
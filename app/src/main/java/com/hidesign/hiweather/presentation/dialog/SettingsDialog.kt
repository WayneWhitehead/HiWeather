package com.hidesign.hiweather.presentation.dialog

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hidesign.hiweather.R
import com.hidesign.hiweather.presentation.components.SettingsDropdownMenu
import com.hidesign.hiweather.presentation.components.SettingsSwitch
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.Constants
import com.permissionx.guolindev.PermissionX

@Composable
fun SettingsDialog(showSettings: Boolean, onSettingsChanged: (Int) -> Unit) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
    var posInterval by remember { mutableIntStateOf(sharedPref.getInt(APIWorker.REFRESH_INTERVAL, 1)) }
    val intervals = Constants.refreshIntervals

    var weatherUpdates by remember { mutableStateOf(sharedPref.getBoolean(APIWorker.WEATHER_UPDATES, true)) }
    var airUpdates by remember { mutableStateOf(sharedPref.getBoolean(APIWorker.AIR_UPDATES, true)) }

    AnimatedVisibility(showSettings) {
        val isNotificationEnabled by remember { mutableStateOf(PermissionX.areNotificationsEnabled(context)) }
        Dialog(
            onDismissRequest = { onSettingsChanged(posInterval) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(horizontal = 10.dp),
                            onClick = { onSettingsChanged(posInterval) }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                        }

                        Text(
                            text = stringResource(id = R.string.settings),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    SettingsDropdownMenu(
                        items = intervals.toList(),
                        selectedPos = posInterval,
                        isError = isNotificationEnabled,
                        onItemSelected = {
                            posInterval = it
                            sharedPref.edit().putInt(APIWorker.REFRESH_INTERVAL, it).apply()
                        }
                    )

                    if (posInterval != 0) {
                        SettingsSwitch("Weather Updates", weatherUpdates) { enabled ->
                            weatherUpdates = if (isNotificationEnabled) enabled else false
                            sharedPref.edit().putBoolean(APIWorker.WEATHER_UPDATES, weatherUpdates).apply()
                        }
                        SettingsSwitch("Air Quality Updates", airUpdates) {enabled ->
                            airUpdates = if (isNotificationEnabled) enabled else false
                            sharedPref.edit().putBoolean(APIWorker.AIR_UPDATES, airUpdates).apply()
                        }
                    }
                }
            }
        }
    }
}
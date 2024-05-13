package com.hidesign.hiweather.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hidesign.hiweather.R
import com.hidesign.hiweather.util.Constants

@Composable
fun SettingsDialog(showSettings: Boolean, onSettingsChanged: (Boolean) -> Unit) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)

    var enableNotifications by remember { mutableStateOf(sharedPref.getBoolean(Constants.NOTIFICATION_ENABLED, false)) }

    val units = context.resources.getStringArray(R.array.temperature_units)
    val posUnit by remember { mutableIntStateOf(sharedPref.getInt(Constants.TEMPERATURE_UNIT, 0)) }

    val intervals = context.resources.getStringArray(R.array.refresh_interval)
    val posInterval by remember { mutableIntStateOf(sharedPref.getInt(Constants.REFRESH_INTERVAL, 0)) }

    if (showSettings) {
        Dialog(
            onDismissRequest = { onSettingsChanged(enableNotifications) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Card(
                Modifier.wrapContentSize(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(10.dp, 0.dp),
                            onClick = { onSettingsChanged(enableNotifications) }
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                        }

                        Text(
                            text = stringResource(id = R.string.settings),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Row(
                        Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(modifier = Modifier.fillMaxWidth(), text = "Enable Notifications")
                        Switch(
                            modifier = Modifier.wrapContentSize(align = Alignment.Center),
                            checked = enableNotifications,
                            onCheckedChange = {
                                enableNotifications = it
                                sharedPref.edit().putBoolean(Constants.NOTIFICATION_ENABLED, enableNotifications).apply()
                            }
                        )
                    }

                    if (enableNotifications) {
                        SettingsDropdownMenu(
                            items = intervals.toList(),
                            selectedPos = posInterval,
                            onItemSelected = {
                                sharedPref.edit().putInt(Constants.REFRESH_INTERVAL, it).apply()
                            }
                        )
                    }

                    SettingsDropdownMenu(
                        items = units.toList(),
                        selectedPos = posUnit,
                        onItemSelected = {
                            sharedPref.edit().putInt(Constants.TEMPERATURE_UNIT, it).apply()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownMenu(
    items: List<String>,
    selectedPos: Int,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(items[selectedPos]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { onItemSelected(items.indexOf(it)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDownCircle,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onItemSelected(items.indexOf(selectionOption))
                    },
                    text = {
                        Text(text = selectionOption)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsDialog() {
    SettingsDialog(true) {

    }
}
package com.hidesign.hiweather.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hidesign.hiweather.R
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.Constants

@Composable
fun SettingsDialog() {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)

    val units = context.resources.getStringArray(R.array.temperature_units)
    val posUnit = sharedPref.getInt(Constants.TEMPERATURE_UNIT, 0)

    val intervals = context.resources.getStringArray(R.array.refresh_interval)
    val posInterval = sharedPref.getInt(Constants.REFRESH_INTERVAL, 0)

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = {
            showSettings.value = false
            APIWorker.initWorker(context)
        }
    ) {
        Card (
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = MaterialTheme.colors.background,) {
            Column {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp, 0.dp),
                        onClick = { showSettings.value = false}
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "", tint = Color.White)
                    }

                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                }

                SettingsDropdownMenu(
                    items = units.toList(),
                    posUnit,
                    onItemSelected = {
                        sharedPref.edit().putInt(Constants.TEMPERATURE_UNIT, it).apply()
                    }
                )
                SettingsDropdownMenu(
                    items = intervals.toList(),
                    posInterval,
                    onItemSelected = {
                        sharedPref.edit().putInt(Constants.REFRESH_INTERVAL, it).apply()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsDropdownMenu(
    items: List<String>,
    selectedPos: Int,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(items[selectedPos]) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { onItemSelected(items.indexOf(it)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDownCircle,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
            },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onItemSelected(items.indexOf(selectionOption))
                    }
                ){
                    Text(text = selectionOption)
                }
            }
        }
    }
}
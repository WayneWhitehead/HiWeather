package com.hidesign.hiweather.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.twotone.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownMenu(
    items: List<String>,
    selectedPos: Int,
    isError: Boolean,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(items[selectedPos]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            trailingIcon = {
                if (isError) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }  else {
                    Icon(imageVector = Icons.TwoTone.NotificationsOff, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            label = { Text(text = "Notification Update Interval") },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onItemSelected(items.indexOf(selectionOption))
                    },
                    text = { Text(text = selectionOption) },
                )
            }
        }
    }
}

@Composable
fun SettingsSwitch(title: String, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = enabled,
            onCheckedChange = {
                onCheckedChange(it)
            },
            thumbContent = {
                Icon(
                    modifier = Modifier.padding(2.dp),
                    imageVector = if (enabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                    contentDescription = "",
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        )
    }
}
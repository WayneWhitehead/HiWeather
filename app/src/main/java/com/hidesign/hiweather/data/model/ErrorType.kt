package com.hidesign.hiweather.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ErrorType(val message: String, val icon: ImageVector) {
    PLACES_ERROR("Unable to fetch places.", Icons.Outlined.WrongLocation),
    LOCATION_ERROR("Unable to fetch location.", Icons.Outlined.LocationDisabled),
    LOCATION_PERMISSION_ERROR("Location permission denied.", Icons.Outlined.LocationSearching),
    WEATHER_ERROR("Unable to fetch weather.", Icons.Outlined.ErrorOutline),
}
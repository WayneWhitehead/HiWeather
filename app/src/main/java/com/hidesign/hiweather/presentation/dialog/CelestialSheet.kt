package com.hidesign.hiweather.presentation.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hidesign.hiweather.presentation.AdViewComposable
import com.hidesign.hiweather.presentation.WeatherViewModel
import com.hidesign.hiweather.presentation.components.LunarCard
import com.hidesign.hiweather.presentation.components.SolarCard
import com.hidesign.hiweather.util.AdUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelestialSheet(state: WeatherViewModel.SunMoonDialogState, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        modifier = Modifier.padding(0.dp, 0.dp , 0.dp, 20.dp),
        containerColor = Color(0xD9000000),
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        SolarCard(Modifier.padding(horizontal = 30.dp), daily = state.daily, tz = state.timezone)
        HorizontalDivider(Modifier.padding(vertical = 15.dp), color = Color.White)
        LunarCard(Modifier.padding(horizontal = 30.dp), daily = state.daily, tz = state.timezone)
        HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color.Transparent)
        AdViewComposable(modifier = Modifier, adUnitId = AdUtil.BOTTOM_SHEET_AD)
    }
}
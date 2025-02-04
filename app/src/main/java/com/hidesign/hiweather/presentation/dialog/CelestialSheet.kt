package com.hidesign.hiweather.presentation.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hidesign.hiweather.data.model.Daily
import com.hidesign.hiweather.presentation.AdViewComposable
import com.hidesign.hiweather.presentation.components.LunarCard
import com.hidesign.hiweather.presentation.components.SolarCard
import com.hidesign.hiweather.util.AdUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelestialSheet(daily: Daily, tz: String, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        modifier = Modifier.padding(0.dp, 0.dp , 0.dp, 20.dp),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        SolarCard(Modifier.padding(horizontal = 30.dp), daily = daily, tz = tz)
        HorizontalDivider(Modifier.padding(vertical = 15.dp), color = Color.White)
        LunarCard(Modifier.padding(horizontal = 30.dp), daily = daily, tz = tz)
        HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color.Transparent)
        AdViewComposable(modifier = Modifier, adUnitId = AdUtil.BOTTOM_SHEET_AD)
    }
}
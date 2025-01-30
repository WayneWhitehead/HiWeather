package com.hidesign.hiweather.presentation.dialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hidesign.hiweather.data.model.Current
import com.hidesign.hiweather.presentation.CompassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WindSheet(
    current: Current,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = Color.White,
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = onDismissRequest
    ) {
        CompassView(current)
    }
}

@Composable
fun CompassView(current: Current, compassViewModel: CompassViewModel = hiltViewModel()) {
    val userDirection by compassViewModel.userDirectionFlow.collectAsState(initial = 0f)
    val animatedUserDirection by animateFloatAsState(targetValue = userDirection)

    Box(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(300.dp).rotate(-animatedUserDirection)) {
            CompassCircle(current)

            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("wind_anim.json"))
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxSize()
                    .rotate((-(270-current.windDeg)).toFloat())
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
fun CompassCircle(current: Current) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (north, northLine, east, south, west, circle, windSpeed) = createRefs()

        Text(
            text = "N",
            fontSize = 24.sp,
            color = Color.Red,
            modifier = Modifier.constrainAs(north) {
                top.linkTo(parent.top, 8.dp)
                centerHorizontallyTo(parent)
            }
        )

        Box(modifier = Modifier.size(2.dp, 100.dp).constrainAs(northLine) {
            top.linkTo(north.bottom)
            start.linkTo(north.start)
            end.linkTo(north.end)
            bottom.linkTo(circle.bottom, 150.dp)
        }) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Red))
        }

        Text(
            text = "E",
            fontSize = 24.sp,
            modifier = Modifier.constrainAs(east) {
                end.linkTo(parent.end, 8.dp)
                centerVerticallyTo(parent)
            }
        )

        Text(
            text = "S",
            fontSize = 24.sp,
            modifier = Modifier.constrainAs(south) {
                bottom.linkTo(parent.bottom, 8.dp)
                centerHorizontallyTo(parent)
            }
        )

        Text(
            text = "W",
            fontSize = 24.sp,
            modifier = Modifier.constrainAs(west) {
                start.linkTo(parent.start, 8.dp)
                centerVerticallyTo(parent)
            }
        )

        Canvas(modifier = Modifier.constrainAs(circle) {
            top.linkTo(north.bottom)
            bottom.linkTo(south.top)
            start.linkTo(west.end)
            end.linkTo(east.start)
        }.fillMaxSize()) {
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Row(
            modifier = Modifier.wrapContentSize().constrainAs(windSpeed) { centerTo(circle) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${current.windSpeed*3.6}",

                fontSize = 24.sp,
            )
            Text(
                text = "km/h",
                color = Color.Gray,
                fontSize = 16.sp,
            )

        }
    }
}
package com.hidesign.hiweather.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun <T> ShimmerCrossfade(
    modifier: Modifier,
    shimmerHeight: Dp,
    shimmerColour: Color,
    data: T?,
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier.wrapContentHeight()) {
        AnimatedVisibility(
            visible = data == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ShimmerEffect(
                modifier = Modifier.height(shimmerHeight),
                color = shimmerColour,
            )
        }

        AnimatedVisibility(
            visible = data != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            data?.let {
                content(it)
            }
        }
    }
}
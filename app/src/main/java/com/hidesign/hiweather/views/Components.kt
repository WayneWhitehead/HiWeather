package com.hidesign.hiweather.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hidesign.hiweather.util.AdUtil

@Composable
fun LoadPicture(modifier: Modifier = Modifier, url: String, contentDescription: String = "", contentScale: ContentScale = ContentScale.Fit) {
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    Glide.with(LocalContext.current)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }
            override fun onLoadCleared(placeholder: Drawable?) {}
        })

    Image(
        painter = rememberAsyncImagePainter(bitmapState.value),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
fun ForecastImageLabel(forecastItem: String, image: Painter, size: Int = 15) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Image(
            painter = image,
            modifier = Modifier.size(size.dp),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "$forecastItem Icon"
        )

        Text(
            text = forecastItem,
            fontSize = size.sp,
            color = Color.White
        )
    }
}

@Composable
fun ForecastIconLabel(forecastItem: String, icon: ImageVector, size: Int = 15) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Image(
            imageVector = icon,
            modifier = Modifier.size(size.dp),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "$forecastItem Icon"
        )

        Text(
            text = forecastItem,
            fontSize = size.sp,
            color = Color.White
        )
    }
}

@Composable
fun AdViewComposable(modifier: Modifier, adUnitId: String) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            AdUtil.setupAds(ctx, adUnitId)
        }
    )
}
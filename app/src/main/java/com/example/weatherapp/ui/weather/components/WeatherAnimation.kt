package com.example.weatherapp.ui.weather.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R

@Composable
fun WeatherAnimation(
    weatherType: String,
    modifier: Modifier = Modifier
) {

    val animationRes = when (weatherType) {
        "Clear" -> R.raw.sunny
        "Clouds" -> R.raw.cloudy
        "Rain", "Drizzle" -> R.raw.rain
        "Thunderstorm" -> R.raw.rain
        "Snow" -> R.raw.cloudy
        else -> R.raw.cloudy
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationRes)
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.size(200.dp)
    )
}
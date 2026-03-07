package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R

@Composable
fun WeatherAnimation(
    weatherType: String
) {

    val animationRes = when (weatherType) {

        "Clear" -> R.raw.sunny

        "Clouds" -> R.raw.cloudy

        "Rain" -> R.raw.rain

        else -> R.raw.cloudy
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationRes)
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(160.dp)
    )
}
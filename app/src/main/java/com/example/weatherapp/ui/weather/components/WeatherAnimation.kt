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
import com.example.weatherapp.utils.WeatherAnimationMapper

@Composable
fun WeatherAnimation(
    weatherType: String,
    modifier: Modifier = Modifier
) {

    val animationRes = WeatherAnimationMapper.getAnimationRes(weatherType)

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationRes)
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.size(200.dp)
    )
}
package com.example.weatherapp.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun applyAppLanguage(language: String) {

    val localeList = LocaleListCompat.forLanguageTags(language)

    AppCompatDelegate.setApplicationLocales(localeList)
}
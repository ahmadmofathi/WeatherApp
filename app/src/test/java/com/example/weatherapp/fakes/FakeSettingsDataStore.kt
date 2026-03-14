package com.example.weatherapp.fakes

import com.example.weatherapp.data.preferences.SettingsProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDataStore : SettingsProvider {

    private val _temperatureUnit = MutableStateFlow("metric")
    override val temperatureUnit: Flow<String> = _temperatureUnit

    private val _windSpeedUnit = MutableStateFlow("mps")
    override val windSpeedUnit: Flow<String> = _windSpeedUnit

    private val _language = MutableStateFlow("en")
    override val language: Flow<String> = _language

    private val _alertsEnabled = MutableStateFlow(false)
    override val alertsEnabled: Flow<Boolean> = _alertsEnabled

    private val _alertCondition = MutableStateFlow("Rain")
    override val alertCondition: Flow<String> = _alertCondition

    fun setUnit(unit: String) { _temperatureUnit.value = unit }
    fun setWindUnit(unit: String) { _windSpeedUnit.value = unit }
    fun setLang(lang: String) { _language.value = lang }
}

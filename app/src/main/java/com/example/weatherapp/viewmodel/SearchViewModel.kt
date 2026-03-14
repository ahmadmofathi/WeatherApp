package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.dto.GeoLocation
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(

    private val repository: WeatherRepository

) : ViewModel() {

    private val _results =
        MutableStateFlow<List<GeoLocation>>(emptyList())

    val results: StateFlow<List<GeoLocation>> = _results

    fun search(query: String) {

        viewModelScope.launch {

            if (query.length < 2) return@launch

            try {

                val response =
                    repository.searchCity(query)

                _results.value = response

            } catch (e: Exception) {

                _results.value = emptyList()
            }
        }
    }
    fun clearResults() {
        _results.value = emptyList()
    }
}

class SearchViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {

            return SearchViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
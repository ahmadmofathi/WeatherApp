package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.dto.GeoLocation
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(

    private val repository: WeatherRepository

) : ViewModel() {

    private val _results =
        MutableStateFlow<List<GeoLocation>>(emptyList())

    val results: StateFlow<List<GeoLocation>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Autocomplete query flow with debounce
    private val _query = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .filter { it.length >= 2 }
                .distinctUntilChanged()
                .collectLatest { query ->
                    _isLoading.value = true
                    try {
                        val response = repository.searchCity(query)
                        _results.value = response
                    } catch (e: Exception) {
                        _results.value = emptyList()
                    }
                    _isLoading.value = false
                }
        }
    }

    fun search(query: String) {
        _query.value = query
        if (query.length < 2) {
            _results.value = emptyList()
        }
    }

    fun clearResults() {
        _results.value = emptyList()
        _query.value = ""
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
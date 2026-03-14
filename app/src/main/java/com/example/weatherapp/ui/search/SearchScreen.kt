package com.example.weatherapp.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.viewmodel.SearchViewModel

@Composable
fun SearchScreen(

    viewModel: SearchViewModel,

    onCitySelected: (Double, Double) -> Unit,

    onMapClick: () -> Unit

) {

    var query by remember { mutableStateOf("") }

    val results by viewModel.results.collectAsState()

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {

        OutlinedTextField(

            value = query,

            onValueChange = {

                query = it

                viewModel.search(it)

            },

            modifier = Modifier.fillMaxWidth(),

            label = { Text("Search city") }

        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(results) { city ->

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            onCitySelected(city.lat, city.lon)
                            viewModel.clearResults()

                        }
                        .padding(12.dp)

                ) {

                    Text(
                        text = "${city.name}, ${city.country}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(

            onClick = onMapClick,

            modifier = Modifier.fillMaxWidth()

        ) {

            Text("Select From Map")

        }
    }
}
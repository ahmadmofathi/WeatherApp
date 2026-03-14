package com.example.weatherapp.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.viewmodel.SearchViewModel

@Composable
fun SearchScreen(

    viewModel: SearchViewModel,

    onCitySelected: (Double, Double) -> Unit,

    onMapClick: () -> Unit

) {

    var query by remember { mutableStateOf("") }

    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {

        Text(
            text = stringResource(R.string.search_city),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(

            value = query,

            onValueChange = {
                query = it
                viewModel.search(it)
            },

            modifier = Modifier.fillMaxWidth(),

            label = { Text(stringResource(R.string.enter_city_name)) },

            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },

            shape = RoundedCornerShape(16.dp),

            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // No results message
        if (query.length >= 2 && results.isEmpty() && !isLoading) {
            Text(
                text = stringResource(R.string.no_results_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(results) { city ->

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCitySelected(city.lat, city.lon)
                            viewModel.clearResults()
                        }
                        .padding(12.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${city.name}, ${city.country}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(

            onClick = onMapClick,

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            shape = RoundedCornerShape(14.dp)

        ) {

            Icon(
                Icons.Outlined.Map,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                stringResource(R.string.select_from_map),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
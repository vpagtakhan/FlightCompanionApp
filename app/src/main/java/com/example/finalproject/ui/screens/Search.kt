package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.data.model.FlightData
import com.example.finalproject.data.model.FlightResponse
import com.example.finalproject.ui.components.FlightResultCard
import com.example.finalproject.ui.viewmodel.FlightSearchViewModel
import com.example.finalproject.ui.viewmodel.SearchMode

/**
 * This screen allows the user to search for flights
 *
 * This composable allows:
 * The user to have the option to search either via Flight Number or Route using Airport Iatas.
 * After clicking the "Search" button, the system then gives the user API data based on the user's inputted search.
 */
@Composable
fun Search(){

    val vm: FlightSearchViewModel = viewModel()

    var searchMode by remember { mutableStateOf(SearchMode.FLIGHT_NUMBER) }

    var flightNumber by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var arr by remember { mutableStateOf("") }

    val results by vm.results.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    val saveMessage by vm.saveMessage.collectAsState()

    Column(modifier = Modifier.padding(24.dp))
    {
        Text("Search for flights", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Row {
            FilterChip(
                selected = searchMode == SearchMode.FLIGHT_NUMBER,
                onClick = { searchMode = SearchMode.FLIGHT_NUMBER },
                label = { Text("Flight Number") }
            )
            Spacer(Modifier.height(8.dp))
            FilterChip(
                selected = searchMode == SearchMode.ROUTE,
                onClick = { searchMode = SearchMode.ROUTE },
                label = { Text("By Route") }
            )
        }

        Spacer(Modifier.height(16.dp))

        when (searchMode) {
            SearchMode.FLIGHT_NUMBER -> {
                TextField(
                    value = flightNumber,
                    onValueChange = { flightNumber = it },
                    label = { Text("Flight Number (e.g. AC123)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SearchMode.ROUTE -> {
                TextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text(" Departure Airport Code (e.g. YWG)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = arr,
                    onValueChange = { arr = it },
                    label = { Text("Arrival Airport Code (e.g. YYZ)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                vm.clearSaveMessage()
                when(searchMode) {
                    SearchMode.FLIGHT_NUMBER -> vm.searchByFlightNumber(flightNumber)
                    SearchMode.ROUTE -> vm.searchByFlightRoute(dept, arr)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        if (saveMessage != null) {
            Text(
                text = saveMessage ?: "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(24.dp))

        when {
            isLoading -> Text("Loading...")

            error != null -> Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )

            results.isNotEmpty() -> {
                Text("Results:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                results.forEach { data: FlightData ->
                    FlightResultCard(
                        data = data,
                        onSaveClicked = { vm.saveToFavourites(data) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
            !isLoading && results.isEmpty() && error == null -> {
                Text("Enter information and tap Search.")
            }
        }
    }
}

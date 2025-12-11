package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.data.model.FlightData
import com.example.finalproject.ui.viewmodel.FeaturedFlightsViewModel

/**
 * This is the primary home screen for the Flight Companion App
 *
 * This Composable:
 * Displays a welcome message and a short description of the app
 * Displays a featured list of flights backed by the [FeaturedFlightsViewModel]
 */
@Composable
fun Home() {

    val vm: FeaturedFlightsViewModel = viewModel()
    val featured by vm.featured.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    //loads featured flights right when the screen enters composition
    LaunchedEffect(Unit) {
        vm.loadFeaturedFlights()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Welcome to the Flight Companion App!",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Track your flights, explore other flights, and save your favourites!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Featured Flights",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
        }

        when {
            isLoading -> {
                item { Text("Loading Featured Flights...") }
            }

            error != null -> {
                item {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            featured.isEmpty() -> {
                item { Text("No featured Flights are available right now.") }
            }

            else -> {
                items(featured) { data: FlightData ->
                    FeaturedFlightCardFromApi(data)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

/**
 * Card UI that displays a single featured flight pulled from the Aviationstack API
 *
 * The card shows:
 * A departure airport and its code
 * An arrival airport and its code
 * A status of whether it is On Time, Cancelled, or has Landed.
 *
 * @param data The flight information returned by the API.
 * @param showStatus Whether to show the derived status text.
 */
@Composable
fun FeaturedFlightCardFromApi(data: FlightData,
                              showStatus: Boolean = true) {

    val departureAirport = data.departure?.airport ?: "Unknown Airport"
    val departureIata = data.departure?.iata ?: ""
    val arrivalAirport = data.arrival?.airport ?: "Unknown Airport"
    val arrivalIata = data.arrival?.iata ?: ""

    val airlineName = data.airline?.name ?: "Unknown airline"
    val flightNumber = data.flight?.number ?: "Unknown number"

    val departureTime = data.departure?.scheduled ?: "No scheduled departure."

    val rawStatus = data.flight_status?.lowercase()

    val statusText = when(rawStatus) {
        "cancelled" -> "Cancelled"
        "landed" -> "Landed"
        "active", "scheduled" -> "On time"
        else -> data.flight_status ?: "Unknown"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "$departureAirport ($departureIata)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = "Flight",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$arrivalAirport ($arrivalIata)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "$airlineName - $flightNumber",
                style = MaterialTheme.typography.bodySmall
            )

            if (departureTime.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Departure: $departureTime",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if(showStatus && statusText.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Status: $statusText",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

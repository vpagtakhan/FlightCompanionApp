package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.data.model.SavedFlight
import com.example.finalproject.ui.viewmodel.FavouritesViewModel

/**
 * Screen that displays the users' favourited flights.
 *
 * This composable:
 * -Loads favourites from the [FavouritesViewModel]
 * -Shows loading, error, empty, or listed states.
 * -Provides a Refresh button to re-call the API to refresh the status of a flight.
 * -Allows a user to remove individual favourited flights.
 */
@Composable
fun FavouriteFlights() {
    val vm: FavouritesViewModel = viewModel()

    val favourites by vm.favourites.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()

    // Load favourited flights when the screen is first shown
    LaunchedEffect(Unit) {
        vm.loadFavourites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ){
        Text(
            text = "Your Favourite Flights",
            style = MaterialTheme.typography.headlineSmall
        )

        TextButton(
            onClick = { vm.refreshStatus() },
            enabled = !isRefreshing && favourites.isNotEmpty()
        ) {
            if(isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Refresh")
            }
        }
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Text("Loading Favourites...")
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            favourites.isEmpty() -> {
                Text("No favourite flights yet. Search for a flight and click \"Save to favourites\".")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    items(favourites) { saved ->
                        FavouriteFlightsCard(
                            saved = saved,
                            onDelete = { vm.deleteFavourites(saved) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * This composable is the single card UI for a favourited flight.
 *
 * Shows: Airline Name, along with the flight number.
 * Departure airport and its airport code/iata
 * Arrival airport and its airport code/iata
 *
 * @param saved The favourited flight that is to be displayed
 * @param onDelete The callback we use when the user taps "remove".
 */
@Composable
fun FavouriteFlightsCard(
    saved: SavedFlight,
    onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(Modifier.padding(16.dp)){
            Text(
                text = "${saved.airlineName.ifBlank { "Unknown Airline" }} - ${saved.flightNumber.ifBlank { "Unknown Flight" }}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "From: ${saved.departureAirport.ifBlank { "-" }} (${saved.departureIata.ifBlank { "" }})",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "To: ${saved.arrivalAirport.ifBlank { "-" }} (${saved.arrivalIata.ifBlank { "" }})"
            )

            Spacer(Modifier.height(4.dp))

            if(saved.status.isNotBlank()) {
                Text(
                    text = "Last known status: ${saved.status}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End)
            {
                TextButton(onClick = onDelete) {
                    Text("Remove")
                }
            }
        }
    }
}
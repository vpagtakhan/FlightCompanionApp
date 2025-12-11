package com.example.finalproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finalproject.data.model.FlightData
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * This Card UI displays information about a single flight
 *
 * This composable does:
 * Gives information on departure and its arrival airports
 * Gives the Airline name and its Flight Number
 * Then gives a status of whether it is "On Time", "Cancelled", or "Landed"
 * Allows the user to save the flight to favourites with a click of a button.
 *
 * @param data The data that comes from the API
 * @param onSaveClicked Callback for when the user clicks "Save to favourites" to save the flight
 * @param showStatus Determines whether to display the statuses of "On Time", "Cancelled", or "Landed".
 */
@Composable
fun FlightResultCard(
    data: FlightData,
    onSaveClicked: () -> Unit,
    showStatus: Boolean = true) {

    val departureAirport = data.departure?.airport ?: "Unknown Airport"
    val departureIata = data.departure?.iata ?: ""
    val arrivalAirport = data.arrival?.airport ?: "Unknown Airport"
    val arrivalIata = data.arrival?.iata ?: ""

    val airlineName = data.airline?.name ?: "Unknown airline"
    val flightNumber = data.flight?.number ?: "Unknown number"

    val departureTime = formatDepartureTime(data.departure?.scheduled)

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

            Button(
                onClick = onSaveClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to Favourites")
            }
        }
    }
}

/**
 * This function allows to format the ISO string that the API data gives us
 * Into a proper date OR time format.
 *
 * @param isoString This is the original string that we pull from the API data.
 */
private fun formatDepartureTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        val odt = OffsetDateTime.parse(isoString)
        odt.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))
    } catch (e: Exception) {
        ""
    }
}
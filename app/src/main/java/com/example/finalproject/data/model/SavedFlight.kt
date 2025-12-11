package com.example.finalproject.data.model

/**
 * Stored information of a flight that a user has saved
 *
 * Simplified version from the FlightData class and kept only the data
 * That will be used to be displayed in the Favourites screen.
 */
data class SavedFlight (
    val flightNumber: String = "",
    val airlineName: String = "",
    val departureAirport: String = "",
    val departureIata: String = "",
    val arrivalAirport: String = "",
    val arrivalIata: String = "",
    val status: String = "",
    val userId: String = "",
    val id: String = ""
)
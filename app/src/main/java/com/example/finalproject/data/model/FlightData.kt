package com.example.finalproject.data.model

/**
 * Single flight entry returned from the Aviationstack "flights" endpoint.
 *
 * This wraps the high-level information about a flight, including:
 * - Overall flight status (e.g. "active", "landed", "cancelled").
 * - Airline details.
 * - Flight number.
 * - Departure and arrival airport info.
 *
 * @property flight_status Raw status string from the API
 * (e.g. "active", "landed", "cancelled", "scheduled").
 * @property airline Airline information such as display name.
 * @property flight Flight number and related identifiers.
 * @property departure Departure airport details (name, IATA code, times).
 * @property arrival Arrival airport details (name, IATA code, times).
 */
data class FlightData(
    val flight_status: String?,
    val airline: Airline?,
    val flight: FlightInfo?,
    val departure: AirportInfo?,
    val arrival: AirportInfo?
)
/**
 * Airline information for a flight.
 *
 * @property name Display name of the airline (e.g. "Air Canada").
 */
data class Airline (
    val name: String?
)
/**
 * Basic flight identifiers.
 *
 * @property number Airline flight number in IATA format (e.g. "AC430").
 */
data class FlightInfo(
    val number: String?
)
/**
 * Airport and timing information used for both departure and arrival.
 *
 * @property airport Full airport name (e.g. "Winnipeg International Airport").
 * @property iata IATA code for the airport (e.g. "YWG").
 * @property scheduled Scheduled date/time in the format returned by the API.
 * @property delay Delay in minutes, if the API provides it, or null if unknown.
 */
data class AirportInfo(
    val airport: String?,
    val scheduled: String?,
    val iata: String?,
    val delay: Int?
)

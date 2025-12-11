package com.example.finalproject.data.repository

import com.example.finalproject.BuildConfig
import com.example.finalproject.data.model.FlightData
import com.example.finalproject.data.remote.RetrofitInstance
/**
 * Repository responsible for fetching flight data from the Aviationstack API.
 *
 * This class wraps the Retrofit API service and exposes simple methods for:
 * - Searching flights by flight number.
 * - Searching flights by departure/arrival route.
 * - Loading a small set of "featured" flights for the Home screen.
 *
 * The rest of the app (ViewModels / UI) should use this repository instead of
 * calling Retrofit or the API service directly.
 */
class FlightRepository {

    /**
     * Fetches flights matching a specific flight number from the Aviationstack API.
     *
     * @param flightNumber Airline flight number entered by the user (e.g. "AC430").
     * @return A [FlightResponse] containing a list of matching [FlightData] items.
     */
    suspend fun getFlightByNumber(flightNumber: String) =
        RetrofitInstance.api.getFlightByNumber(
            apiKey = BuildConfig.FLIGHT_API_KEY,
            flightNumber = flightNumber
        )
    /**
     * Fetches flights matching a departure and arrival airport route.
     *
     * For example, searching from "YWG" to "YUL" returns all flights matching
     * that route within the current time window as defined by the API.
     *
     * @param departureIata IATA code for the departure airport (e.g. "YWG").
     * @param arrivalIata IATA code for the arrival airport (e.g. "YUL").
     * @return A [FlightResponse] with a list of [FlightData] entries for the route.
     */
    suspend fun getFlightByRoute(departure: String, arrival: String): List<FlightData> {
        val response = RetrofitInstance.api.getFlightByRoute(
            apiKey = BuildConfig.FLIGHT_API_KEY,
            depIata = departure,
            arrIata = arrival
        )
        return response.data ?: emptyList()
    }

    /**
     * This fetches departures from a specific Iata which will be used for [FeaturedFlightCardFromApi]
     *
     * @param airportIata The departure airport Iata
     */
    suspend fun getDeparturesFrom(airportIata: String): List<FlightData> {
        val response = RetrofitInstance.api.getDeparturesFrom(
            apiKey = BuildConfig.FLIGHT_API_KEY,
            depIata = airportIata
        )
        return response.data ?: emptyList()
    }

    /**
     * This fetches arrivals from a specific Iata which will be used for [FeaturedFlightCardFromApi]
     *
     * @param airportIata The arrival airport Iata
     */
    suspend fun getArrivalsTo(airportIata: String): List<FlightData> {
        val response = RetrofitInstance.api.getArrivalsTo(
            apiKey = BuildConfig.FLIGHT_API_KEY,
            arrIata = airportIata
        )
        return response.data ?: emptyList()
    }
}
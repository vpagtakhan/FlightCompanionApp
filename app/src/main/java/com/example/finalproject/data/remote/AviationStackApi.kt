package com.example.finalproject.data.remote

import com.example.finalproject.data.model.FlightResponse
import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Retrofit API definition for the Aviationstack flights endpoint.
 *
 * All functions here call the same "flights" endpoint but filter the results
 * using different query parameters such as flight number, departure airport,
 * or arrival airport.
 */
interface AviationStackApi {
    @GET("flights")
    suspend fun getFlightByNumber(
        @Query("access_key") apiKey: String,
        @Query("flight_iata") flightNumber: String,
        @Query("limit") limit: Int = 5
    ): FlightResponse

    @GET("flights")
    suspend fun getFlightByRoute(
        @Query("access_key") apiKey: String,
        @Query("dep_iata") depIata: String,
        @Query("arr_iata") arrIata: String,
        @Query("limit") limit: Int = 5
    ): FlightResponse

    @GET("flights")
    suspend fun getDeparturesFrom(
        @Query("access_key") apiKey: String,
        @Query("dep_iata") depIata: String,
        @Query("limit") limit: Int = 10
    ): FlightResponse

    @GET("flights")
    suspend fun getArrivalsTo(
        @Query("access_key") apiKey: String,
        @Query("arr_iata") arrIata: String,
        @Query("limit") limit: Int = 10
    ): FlightResponse
}
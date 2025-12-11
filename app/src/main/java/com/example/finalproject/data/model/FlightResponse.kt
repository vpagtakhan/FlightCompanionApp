package com.example.finalproject.data.model
/**
 * Top-level response from the Aviationstack "flights" endpoint.
 *
 * This wraps a list of [FlightData] entries returned by the API. Each item
 * in [data] represents a single flight with its status, airline, route, etc.
 *
 * @property data List of flights returned by the query. May be empty if no
 * results were found for the given filters.
 */
data class FlightResponse(
    val data: List<FlightData>?
)
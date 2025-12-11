package com.example.finalproject.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Singleton that provides a configured Retrofit client for the Aviationstack API.
 *
 * This object is responsible for:
 * - Creating the Retrofit instance with the correct base URL.
 * - Registering the Gson converter for JSON serialization/deserialization.
 * - Exposing a lazily-initialized [AviationStackApi] service used by repositories.
 */
object RetrofitInstance {

    private const val BASE_URL = "http://api.aviationstack.com/v1/"

    /**
     * Lazily-created Retrofit API service for Aviationstack.
     *
     * Use this in repositories (e.g. FlightRepository) to call endpoints such as
     * fetching flights by number or by route.
     */
    val api: AviationStackApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AviationStackApi::class.java)
    }
}
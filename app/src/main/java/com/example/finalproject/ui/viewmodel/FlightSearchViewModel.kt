package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.data.model.FlightData
import com.example.finalproject.data.repository.FavouritesRepository
import com.example.finalproject.data.repository.FlightRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supported search modes for the flight search screen.
 *
 * - [FLIGHT_NUMBER]: search using an airline flight number (e.g. "AC430").
 * - [ROUTE]: search using departure and arrival airport IATA codes (e.g. YWG → YUL).
 */
enum class SearchMode{
    FLIGHT_NUMBER,
    ROUTE
}

/**
 * ViewModel responsible for searching flights and saving results to favourites.
 *
 * This ViewModel:
 * - Calls [FlightRepository] to search flights either by flight number or by route.
 * - Exposes search results, loading, and error state as StateFlows for the UI.
 * - Uses [FavouritesRepository] to save selected flights to the user's favourites.
 */
class FlightSearchViewModel (
    private val repo: FlightRepository = FlightRepository(),
    private val favRepo: FavouritesRepository = FavouritesRepository()
): ViewModel() {
    private val _results = MutableStateFlow<List<FlightData>>(emptyList())
    val results = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage = _saveMessage.asStateFlow()
    /**
     * Searches for flights using a flight number (IATA format).
     *
     * Steps:
     * - Normalizes the input by trimming spaces and uppercasing (e.g. "ac 430" → "AC430").
     * - Calls [FlightRepository.getFlightByNumber].
     * - Updates [results] with the returned flights, or sets [error] if none are found.
     *
     * @param flightNumber User input flight number (e.g. "AC430").
     */
    fun searchByFlightNumber(flightNumber: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _results.value = emptyList()

            try { val cleaned = flightNumber.trim().replace(" ", "").uppercase()
                val response = repo.getFlightByNumber(cleaned)
                val data = response.data

                if(data.isNullOrEmpty()) {
                    _error.value = "No flights found for \"$cleaned\"."
                } else {
                    _results.value = data
                }
            } catch (e: Exception) {
                _error.value = "Error searching by flight number."
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Searches for flights using a route (departure and arrival IATA codes).
     *
     * Steps:
     * - Normalizes both codes by trimming and uppercasing.
     * - Calls [FlightRepository.getFlightByRoute].
     * - Updates [results] with the returned flights, or sets [error] if none are found.
     *
     * @param deptIata Departure airport IATA code (e.g. "YWG").
     * @param arrIata Arrival airport IATA code (e.g. "YUL").
     */
    fun searchByFlightRoute(deptIata: String, arrIata: String) {
        viewModelScope.launch {
            _isLoading.value = false
            _error.value = null
            _results.value = emptyList()

            try {
                val dept = deptIata.trim().uppercase()
                val arr = arrIata.trim().uppercase()

                val data = repo.getFlightByRoute(dept, arr)

                if(data.isEmpty()) {
                    _error.value = "No flights found for $dept to $arr"
                } else {
                    _results.value = data
                }
            } catch (e: Exception) {
                _error.value = "Error searching by route."
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Saves a selected flight to the current user's favourites.
     *
     * Delegates to [FavouritesRepository.saveFLightToFavourites] and updates
     * [saveMessage] with the resulting status message (success or failure).
     *
     * @param flight The [FlightData] to save as a favourite.
     */
    fun saveToFavourites(flight: FlightData) {
        favRepo.saveFLightToFavourites(flight) { success, message ->
            _saveMessage.value = message
        }
    }
    /**
     * Clears the current save message.
     *
     * This is typically called by the UI after showing feedback to the user
     * (e.g. "Flight has been saved to favourites.").
     */
    fun clearSaveMessage() {
        _saveMessage.value = null
    }
}

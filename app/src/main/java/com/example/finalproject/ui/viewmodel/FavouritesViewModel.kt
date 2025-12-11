package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.model.SavedFlight
import com.example.finalproject.data.repository.FavouritesRepository
import com.example.finalproject.data.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * ViewModel responsible for managing the user's favourite flights.
 *
 * This ViewModel:
 * - Loads favourite flights from [FavouritesRepository] for the current user.
 * - Exposes favourites, loading, error, and refreshing states as StateFlows for the UI.
 * - Allows removing favourites from Firestore.
 * - Refreshes the "status" field of favourite flights by calling [FlightRepository]
 *   again for each saved flight number.
 *
 * It is used by the [com.example.finalproject.ui.screens.FavouriteFlights] screen.
 */
class FavouritesViewModel(
    private val flightRepo: FlightRepository = FlightRepository(),
    private val favouriteRepo: FavouritesRepository = FavouritesRepository()
): ViewModel() {

    private val _favourites = MutableStateFlow<List<SavedFlight>>(emptyList())
    val favourites = _favourites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    /**
     * Loads the current user's favourite flights from Firestore.
     *
     * This updates:
     * - [isLoading] while the request is in progress.
     * - [favourites] with the loaded list on success.
     * - [error] with a user-readable message on failure.
     */
    fun loadFavourites() {
        _isLoading.value = true
        _error.value = null

        favouriteRepo.getUserFavourites { list, errorMsg ->
            if (errorMsg != null) {
                _error.value = errorMsg
                _favourites.value = emptyList()
            } else {
                _favourites.value = list
            }
            _isLoading.value = false
        }
    }
    /**
     * Deletes a favourite flight from Firestore and updates the local list.
     *
     * If the delete fails, [error] is set with the returned message.
     *
     * @param flight The [SavedFlight] to remove from the favourites list.
     */
    fun deleteFavourites(flight: SavedFlight) {
        favouriteRepo.deleteFavourite(flight) { success, msg ->
            if(!success) {
                _error.value = msg
            } else {
                _favourites.value = _favourites.value.filterNot { it.id == flight.id }
            }
        }
    }
    /**
     * Refreshes the status of all favourite flights by calling the API again.
     *
     * For each [SavedFlight], this:
     * - Calls [FlightRepository.getFlightByNumber] using the saved flight number.
     * - Looks at the first result in the response data.
     * - If a non-blank [flight_status] is found, updates the [SavedFlight.status].
     * - If the API call fails or no status is available, keeps the old entry.
     *
     * This only updates the in-memory [favourites] list and does not write the
     * refreshed status back to Firestore.
     */
    fun refreshStatus() {
        val currentList = _favourites.value
        if(currentList.isEmpty()) return

        _isRefreshing.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val updatedList = currentList.map { saved ->
                    try {
                        val response = flightRepo.getFlightByNumber(saved.flightNumber)
                        val fresh =  response.data?.firstOrNull()

                        if(fresh != null && !fresh.flight_status.isNullOrBlank()) {
                            saved.copy(status = fresh.flight_status!!)
                        } else {
                            saved
                        }
                    } catch (e: Exception) {
                        saved
                    }
                }
                _favourites.value = updatedList
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.model.FlightData
import com.example.finalproject.data.repository.FlightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
/**
 * ViewModel responsible for loading and exposing "featured" flights on the Home screen.
 *
 * This ViewModel:
 * - Calls [FlightRepository] to fetch departures from and arrivals to a base airport (YWG).
 * - Picks a small subset of flights (e.g., 2 departures + 1 arrival) to display as featured.
 * - Exposes loading, error, and featured flight state as StateFlows for the UI.
 */
class FeaturedFlightsViewModel(
    private val repo: FlightRepository = FlightRepository()
) : ViewModel() {

    private val _featured = MutableStateFlow<List<FlightData>>(emptyList())
    val featured = _featured.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    /**
     * Loads a small selection of "featured" flights for a hard-coded base airport (YWG).
     *
     * Implementation details:
     * - Fetches departures from YWG using [FlightRepository.getDeparturesFrom].
     * - Fetches arrivals to YWG using [FlightRepository.getArrivalsTo].
     * - Selects up to two departures and one arrival to display.
     *
     * On success:
     * - Updates [featured] with the selected flights.
     *
     * On failure:
     * - Sets [error] with a user-friendly message.
     * - Leaves [featured] as an empty list.
     */
    fun loadFeaturedFlights() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _featured.value = emptyList()

            try {
                val departures = repo.getDeparturesFrom("YWG")

                val arrivals = repo.getArrivalsTo("YWG")

                val selected = mutableListOf<FlightData>()

                selected.addAll(departures.take(2))

                if (arrivals.isNotEmpty()) {
                    selected.add(arrivals.first())
                }

                _featured.value = selected
            } catch (e: Exception)  {
                _error.value = "Failed to load Featured Flights."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
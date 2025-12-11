package com.example.finalproject.data.repository

import com.example.finalproject.data.model.FlightData
import com.example.finalproject.data.model.SavedFlight
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
/**
 * Repository for managing the user's favourite flights in Firestore.
 *
 * This class:
 * - Saves a snapshot of a [FlightData] as a [SavedFlight] under the current user.
 * - Loads all favourites for the signed-in user.
 * - Deletes a favourite for the signed-in user.
 *
 * All operations are user-scoped using the current [FirebaseAuth] user.
 */
class FavouritesRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
){
    private val db = Firebase.firestore
    /**
     * Saves a flight to the current user's favourites collection in Firestore.
     *
     * The [FlightData] from the API is converted into a simplified [SavedFlight]
     * document. If no user is logged in, the operation fails immediately.
     *
     * @param flight The flight returned from the API that should be saved.
     * @param onResult Callback invoked with:
     * - first: true if the save succeeded, false otherwise.
     * - second: a user-readable message describing the result.
     */
    fun saveFLightToFavourites(
        flight: FlightData,
        onResult: (Boolean, String) -> Unit
    ){
        val user = auth.currentUser
        if(user == null) {
            onResult(false, "You must be logged in to add to your Favourites.")
            return
        }

        val savedFlight = SavedFlight(
            flightNumber =  flight.flight?.number ?: "",
            airlineName = flight.airline?.name ?: "",
            departureAirport = flight.departure?.airport ?: "",
            departureIata = flight.departure?.iata ?: "",
            arrivalAirport = flight.arrival?.airport ?: "",
            arrivalIata = flight.arrival?.iata ?: "",
            status =  flight.flight_status ?: "",
            userId = user.uid
        )

        db.collection("users")
            .document(user.uid)
            .collection("favourites")
            .add(savedFlight)
            .addOnSuccessListener {
                onResult(true, "Flight has been saved to favouriets.")
            }
            .addOnFailureListener {e ->
                onResult(false, "Failed to save: ${e.localizedMessage}")
            }
    }
    /**
     * Loads all favourite flights for the current user from Firestore.
     *
     * If no user is logged in, an empty list is returned along with an error message.
     * Each document is converted to [SavedFlight] and the Firestore document ID is
     * stored in the [SavedFlight.id] field to support later deletion.
     *
     * @param onResult Callback invoked with:
     * - first: the list of favourites (empty if none or on failure).
     * - second: an error message, or null on success.
     */
    fun getUserFavourites(
        onResult: (List<SavedFlight>, String?) -> Unit
    ){
        val user = auth.currentUser
        if( user == null ) {
            onResult(emptyList(), "Please log in to view favourite flights.")
            return
        }

        db.collection("users")
            .document(user.uid)
            .collection("favourites")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val flight = doc.toObject(SavedFlight::class.java)
                    flight?.copy(id = doc.id)
                }
                onResult(list, null)
            }
            .addOnFailureListener { e ->
                onResult(emptyList(), e.localizedMessage ?: "Failed to load favourites.")
            }
    }
    /**
     * Loads all favourite flights for the current user from Firestore.
     *
     * If no user is logged in, an empty list is returned along with an error message.
     * Each document is converted to [SavedFlight] and the Firestore document ID is
     * stored in the [SavedFlight.id] field to support later deletion.
     *
     * @param onResult Callback invoked with:
     * - first: the list of favourites (empty if none or on failure).
     * - second: an error message, or null on success.
     */
    fun deleteFavourite(
        flight: SavedFlight,
        onResult: (Boolean, String) -> Unit
    ){
        val user = auth.currentUser
        if(user == null) {
            onResult(false, "You must be logged in to remove flights.")
            return
        }
        if (flight.id.isBlank()) {
            onResult(false, "Missing documented ID for this flight.")
            return
        }

        db.collection("users")
            .document(user.uid)
            .collection("favourites")
            .document(flight.id)
            .delete()
            .addOnSuccessListener {
                onResult(true, "Removed from list.")
            }
            .addOnFailureListener {e ->
                onResult(false, e.localizedMessage ?: "Failed to remove from list.")
            }
    }
}
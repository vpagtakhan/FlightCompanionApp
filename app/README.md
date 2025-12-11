# Flight Companion ✈️
Mobile App Dev – Final Project

Flight Companion is an Android app built with **Jetpack Compose** that helps users:

- Look up **live flight information** using the **Aviationstack API**
- **Search by flight number** or **by route** (departure + arrival airports)
- **Save flights to favourites** using **Firebase Firestore**
- **Refresh statuses** of favourite flights to see if they are active, delayed, cancelled, or landed

The app is designed with an accessible **dark theme** and follows a simple, user-friendly layout with a **top app bar**, **bottom navigation**, and **navigation drawer** for authentication.

---

## Table of Contents

1. [Features](#features)
2. [Screens](#screens)
3. [Architecture](#architecture)
4. [Tech Stack](#tech-stack)
5. [Project Setup](#project-setup)
6. [How It Works](#how-it-works)
7. [Accessibility Notes](#accessibility-notes)
8. [Known Limitations / Future Improvements](#known-limitations--future-improvements)

---

## Features

### Core Features

- **View Featured Flights**
    - Home screen shows a small list of “featured” flights.
    - Uses the Aviationstack API to fetch **departures from** and **arrivals to** YWG (Winnipeg).
    - Shows basic info like route, airline, flight number, and status.

- **Search Flights**
    - **Search by Flight Number** (e.g. `AC430`).
    - **Search by Route** using departure and arrival IATA codes (e.g. `YWG` → `YUL`).
    - Displays results with:
        - Airline name
        - Flight number
        - Departure and arrival airports
        - Scheduled departure time
        - Flight status (On time / Landed / Cancelled / etc.)

- **Favourites**
    - Logged-in users can **save flights to favourites**.
    - Favourites are stored in **Firebase Firestore** under the user’s account.
    - Users can:
        - View a list of saved flights on the **Favourites** screen.
        - **Remove** flights they no longer want to track.
        - Tap **Refresh** to re-call the API and update the **status** of each saved flight.

### Authentication

- Uses **Firebase Authentication (Email/Password)**.
- App starts in **guest mode**:
    - Users can browse featured flights and search flights without logging in.
    - **Saving to favourites requires login or registration.**
- A **navigation drawer (hamburger menu)** on the left provides:
    - Login
    - Register
    - Logout
    - “Logged in as: [email]” when authenticated

---

## Screens

### Home Screen

- Welcome text and short description.
- “Featured Flights” section:
    - Uses `FeaturedFlightsViewModel` and `FlightRepository`.
    - Shows 2 departures + 1 arrival for a base airport (YWG).
- Each featured flight card shows:
    - Departure & arrival airports + IATA codes
    - Airline name & flight number
    - Scheduled departure time (if available)
    - Status: On time / Landed / Cancelled / etc.

### Search Screen

- Filter chips to choose **Search Mode**:
    - **Flight Number**
    - **Route**
- Flight Number mode:
    - Text field: `AC430`, `WS123`, etc.
- Route mode:
    - Departure IATA (e.g. `YWG`)
    - Arrival IATA (e.g. `YUL`)
- Displays:
    - List of results using a result card component.
    - Each result has a **“Save to favourites”** button (for logged-in users).
- Uses `FlightSearchViewModel`.

### Favourite Flights Screen

- Shows list of **saved flights** from Firestore.
- Each card displays:
    - Airline + flight number
    - From / To airports + IATA codes
    - **Last known status**
- Actions:
    - **Remove** button to delete from favourites.
    - **Refresh** button at top to re-call the API and update status in the UI.
- Uses `FavouritesViewModel` + `FavouritesRepository`.

### Login & Register Screens

- Simple forms using **email + password**.
- Register:
    - Calls `FirebaseAuth.createUserWithEmailAndPassword`.
- Login:
    - Calls `FirebaseAuth.signInWithEmailAndPassword`.
- On success:
    - Toast message.
    - Navigates back into the main app (Home/Search/Favourites).
- Private helper functions like `performSignIn` and `performSignUp` handle:
    - Firebase calls
    - Toast messages
    - Navigation
    - Hiding the soft keyboard

---

## Architecture

The project uses a **basic MVVM architecture**:

- **UI (Compose Screens)**
    - `Home`, `Search`, `FavouriteFlights`, `Login`, `Register`
    - `BottomNav`, `DrawerContent`, `FlightResultCard`, etc.
- **ViewModels**
    - `FlightSearchViewModel`
    - `FeaturedFlightsViewModel`
    - `FavouritesViewModel`
- **Repositories**
    - `FlightRepository` – network / Aviationstack API
    - `FavouritesRepository` – Firestore favourites per user
- **Data / Models**
    - `FlightData`, `FlightResponse`, `Airline`, `FlightInfo`, `AirportInfo`
    - `SavedFlight`
- **Networking**
    - `RetrofitInstance`
    - `AviationStackApi`

Navigation is handled with **Navigation Compose** and a **bottom navigation bar**:

- Routes:
    - `Home`
    - `Search`
    - `Favourites`
- Plus extra routes for:
    - `login`
    - `register`

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Navigation**: Navigation Compose
- **Network**: Retrofit + Gson
- **Backend Services**:
    - Aviationstack API (flight data)
    - Firebase Authentication (email/password)
    - Firebase Firestore (user favourites)
- **Architecture**: MVVM (ViewModel + Repository + StateFlow)

---

## Project Setup

### 1. Clone the project

```bash
git clone https://github.com/vpagtakhan/FlightCompanionApp/tree/master/app
cd /src/main/java/com/example/finalproject
Open the project in Android Studio (Giraffe or newer recommended).

2. Configure Aviationstack API key
Get an API key from aviationstack.com (free tier is enough).

In the project root, add this to your local.properties:

properties
Copy code
FLIGHT_API_KEY=your_api_key_here
The Gradle config reads this and exposes it as BuildConfig.FLIGHT_API_KEY, which is used in FlightRepository and Retrofit API calls.

3. Configure Firebase
Create a Firebase project.

Enable:

Authentication → Email/Password

Firestore Database

Add an Android app in Firebase console with your applicationId, for example:

com.example.finalproject

Download google-services.json and place it in:

app/google-services.json

Make sure the Google Services Gradle plugin is applied in your build.gradle files.

4. Build & Run
Sync Gradle in Android Studio.

Select a device/emulator.

Run the app.

How It Works
Flight Search
By Flight Number

FlightSearchViewModel.searchByFlightNumber(flightNumber) calls FlightRepository.getFlightByNumber.

Repository uses RetrofitInstance.api.getFlightByNumber(...).

FlightResponse.data is exposed via StateFlow and rendered as cards in the UI.

By Route

FlightSearchViewModel.searchByFlightRoute(departureIata, arrivalIata) calls FlightRepository.getFlightByRoute.

The Aviationstack API filters using dep_iata and arr_iata.

Featured Flights
FeaturedFlightsViewModel.loadFeaturedFlights():

Calls:

getDeparturesFrom("YWG")

getArrivalsTo("YWG")

Selects:

Up to 2 departures and 1 arrival as “featured”.

Favourites
Saving a flight

FlightSearchViewModel.saveToFavourites(FlightData) → FavouritesRepository.saveFLightToFavourites.

Converts FlightData → SavedFlight and writes to Firestore under:

users/{userId}/favourites/{docId}

Loading Favourites

FavouritesViewModel.loadFavourites() → FavouritesRepository.getUserFavourites.

Maps documents into SavedFlight, including the Firestore document ID.

Refreshing Status

FavouritesViewModel.refreshStatus():

For each SavedFlight, calls getFlightByNumber(flightNumber) again.

Reads flight_status from the first result.

Updates the in-memory status field shown in the UI.

Accessibility Notes
The app aims to follow WCAG 2.0 AA-ish ideas, including:

Dark theme with sufficient contrast between:

Background

Cards

Text

Clear text hierarchy:

Titles (titleLarge, titleMedium)

Body text (bodyMedium, bodySmall)

Text labels on:

Buttons

Navigation items

Icons (contentDescription provided where appropriate)

Known Limitations / Future Improvements
No offline caching – the app requires network access for:

Flight search

Status refresh

Saved favourites don’t currently persist updated status back into Firestore (statuses are refreshed in-memory only).

No advanced error messages for different HTTP errors (e.g. API quota exceeded vs. network down).

Hard-coded base airport (YWG) for featured flights.

Account management is minimal (no password reset, no email verification).

Credits
Flight Data: Aviationstack API

Backend & Auth: Firebase (Authentication & Firestore)

UI & Navigation: Jetpack Compose + Material 3 + Navigation Compose

Course: Mobile App Development – Red River College Polytech
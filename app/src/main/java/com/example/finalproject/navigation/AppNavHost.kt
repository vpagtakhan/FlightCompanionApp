package com.example.finalproject.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.finalproject.ui.components.BottomNav
import com.example.finalproject.ui.screens.*

/**
 * Main navigation host for the Flight Companion app.
 *
 * This composable:
 * - Hosts the NavHost for all top-level destinations (Home, Search, Favourites, Login, Register).
 * - Shows the [BottomNav] bar only on the main tabs (Home, Search, Favourites).
 * - Keeps track of the current destination to highlight the correct bottom nav item.
 *
 * @param navController The [NavHostController] used to manage app navigation.
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val destination = backStackEntry.value?.destination

    Scaffold(
        bottomBar = {
            if (destination?.route in listOf("home", "search", "favourites")) {
                BottomNav(navController, destination)
            }
        }
    ){ padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home"){ Home() }
            composable("search"){ Search() }
            composable("favourites"){ FavouriteFlights() }
            composable ("login"){ Login() }
            composable ("register"){ Register() }
        }
    }
}
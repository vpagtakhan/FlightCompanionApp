package com.example.finalproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 *Defines the destinations and navigation items used in the [com.example.finalproject.ui.components.BottomNav] class.
 *
 * Each object represents a single-top level screen in the app and provides:
 * @param route The navigation route string used by the NavController
 * @param title The label shown right under the nav Icon.
 * @param icon the [ImageVector] used as the nav Icon.
 */
sealed class NavItems(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home: NavItems("Home", "Home", Icons.Default.Home)
    object Search: NavItems("Search", "Search", Icons.Default.Search)
    object Favourite: NavItems("Favourites", "Favourites", Icons.Default.Favorite)
}
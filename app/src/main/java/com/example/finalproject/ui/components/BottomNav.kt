package com.example.finalproject.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.example.finalproject.navigation.NavItems

/**
 * This is the bottom navigational bar at the bottom of the screen
 *
 * This composable allows us to:
 * Navigate between Home, Search and Favourites.
 * Always starts us on home right when it compiles.
 *
 * @param navController Used to perform navigation actions
 * @param currentDestination The current destination, used to mark the selected nav item.
 */
@Composable
fun BottomNav(
    navController: NavController,
    currentDestination: NavDestination?
){
    val items = listOf(
        NavItems.Home,
        NavItems.Search,
        NavItems.Favourite
    )

    NavigationBar {
        items.forEach { items ->
            val selected = currentDestination?.hierarchy?.any{
                it.route == items.route
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(items.route) {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                },
                icon = { Icon(items.icon, items.title) },
                label = { Text(items.title) }
            )
        }
    }
}

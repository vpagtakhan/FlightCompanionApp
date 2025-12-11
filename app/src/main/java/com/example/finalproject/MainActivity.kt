package com.example.finalproject

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalproject.navigation.AppNavHost
import com.example.finalproject.navigation.NavItems
import com.example.finalproject.ui.components.BottomNav
import com.example.finalproject.ui.screens.FavouriteFlights
import com.example.finalproject.ui.screens.Home
import com.example.finalproject.ui.screens.Login
import com.example.finalproject.ui.screens.Register
import com.example.finalproject.ui.screens.Search
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProjectTheme {
                val navController = rememberNavController()
                AppRoot()
            }

        }
    }
}

/**
 * Root composable for the Flight Companion app.
 *
 * This sets up:
 * - A [ModalNavigationDrawer] with login/register/logout options.
 * - A [Scaffold] containing a top app bar and bottom navigation bar.
 * - A [NavHost] for navigating between Home, Search, Favourites, Login, and Register screens.
 *
 * Behaviour:
 * - The drawer reflects the current Firebase Authentication user (logged in or guest).
 * - Tapping "Login" or "Register" in the drawer navigates to the respective screens.
 * - Tapping "Logout" signs out the current user and navigates back to the Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentUser = FirebaseAuth.getInstance().currentUser

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                isLoggedIn = currentUser != null,
                userEmail = currentUser?.email,
                onLoginClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("login")
                },
                onRegisterClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("register")
                },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    scope.launch { drawerState.close() }
                    navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Flight Companion", color = MaterialTheme.colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                           Icon(Icons.Default.Menu,
                               contentDescription = "Menu",
                               tint = MaterialTheme.colorScheme.onPrimary
                           )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                BottomNav(
                    navController = navController,
                    currentDestination = currentDestination)
            }
        ){ innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable (NavItems.Home.route){ Home() }
                composable (NavItems.Search.route){ Search() }
                composable (NavItems.Favourite.route){ FavouriteFlights() }
                composable ("login"){ Login() }
                composable ("register"){ Register() }
            }
        }
    }
}

/**
 * Drawer content for the side navigation menu.
 *
 * This composable shows:
 * - The app title.
 * - Either a "Logged in as ..." message and a Logout option,
 *   or Login / Register actions if the user is not signed in.
 *
 * @param isLoggedIn Whether there is a currently authenticated user.
 * @param userEmail Email address of the current user, or null if unknown / not logged in.
 * @param onLoginClick Callback invoked when the user taps the "Login" item.
 * @param onRegisterClick Callback invoked when the user taps the "Register" item.
 * @param onLogoutClick Callback invoked when the user taps the "Logout" item.
 */
@Composable
fun DrawerContent (
    isLoggedIn: Boolean,
    userEmail: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit
){
    ModalDrawerSheet {
        Text(
            text = "Flight Companion",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Divider()

        if(isLoggedIn) {
            Text(
                text = "Logged in as: ${userEmail ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                onClick = onLogoutClick
            )
        } else {
            NavigationDrawerItem(
                label = { Text("Login") },
                selected = false,
                onClick = onLoginClick
            )
            NavigationDrawerItem(
                label = { Text("Register") },
                selected = false,
                onClick = onRegisterClick
            )
        }
    }
}

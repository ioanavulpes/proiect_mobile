package com.localpulse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.localpulse.data.repository.AuthRepository
import com.localpulse.data.repository.EventRepository
import com.localpulse.data.repository.FavoritesRepository
import com.localpulse.ui.auth.AuthViewModel
import com.localpulse.ui.auth.AuthViewModelFactory
import com.localpulse.ui.auth.LoginScreen
import com.localpulse.ui.auth.RegisterScreen
import com.localpulse.ui.events.EventDetailsScreen
import com.localpulse.ui.events.EventsScreen
import com.localpulse.ui.events.EventsViewModel
import com.localpulse.ui.events.EventsViewModelFactory
import com.localpulse.ui.favorites.FavoritesScreen
import com.localpulse.ui.favorites.FavoritesViewModel
import com.localpulse.ui.favorites.FavoritesViewModelFactory
import com.localpulse.ui.home.HomeScreen
import com.localpulse.ui.map.MapPlaceholderScreen
import com.localpulse.ui.recommendations.RecommendationsPlaceholderScreen
import com.localpulse.util.Constants

/**
 * Main navigation graph for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    
    // Repositories
    val authRepository = AuthRepository()
    val eventRepository = EventRepository(context)
    val favoritesRepository = FavoritesRepository()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication screens
        composable(Constants.ROUTE_LOGIN) {
            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Constants.ROUTE_REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Constants.ROUTE_HOME) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Constants.ROUTE_REGISTER) {
            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Constants.ROUTE_HOME) {
                        popUpTo(Constants.ROUTE_REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // Home screen
        composable(Constants.ROUTE_HOME) {
            HomeScreen(
                onNavigateToEvents = {
                    navController.navigate(Constants.ROUTE_EVENTS)
                },
                onNavigateToFavorites = {
                    navController.navigate(Constants.ROUTE_FAVORITES)
                },
                onNavigateToMap = {
                    navController.navigate(Constants.ROUTE_MAP)
                },
                onNavigateToRecommendations = {
                    navController.navigate(Constants.ROUTE_RECOMMENDATIONS)
                },
                onLogout = {
                    authRepository.logout()
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(Constants.ROUTE_HOME) { inclusive = true }
                    }
                }
            )
        }

        // Events screen
        composable(Constants.ROUTE_EVENTS) {
            val viewModel: EventsViewModel = viewModel(
                factory = EventsViewModelFactory(eventRepository, favoritesRepository)
            )
            EventsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetails = { eventId ->
                    navController.navigate("event_details/$eventId")
                }
            )
        }

        // Event details screen
        composable(
            route = Constants.ROUTE_EVENT_DETAILS,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val viewModel: EventsViewModel = viewModel(
                factory = EventsViewModelFactory(eventRepository, favoritesRepository)
            )
            EventDetailsScreen(
                eventId = eventId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Favorites screen
        composable(Constants.ROUTE_FAVORITES) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(favoritesRepository)
            )
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Map placeholder screen
        composable(Constants.ROUTE_MAP) {
            MapPlaceholderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Recommendations placeholder screen
        composable(Constants.ROUTE_RECOMMENDATIONS) {
            RecommendationsPlaceholderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


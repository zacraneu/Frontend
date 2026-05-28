package com.example.front.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.front.ui.screens.ApplicationDetailScreen
import com.example.front.ui.screens.ApplicationListScreen
import com.example.front.ui.screens.AdminApplicationDetailScreen
import com.example.front.ui.screens.AdminApplicationListScreen
import com.example.front.ui.screens.CreateApplicationScreen
import com.example.front.ui.screens.EditApplicationScreen
import com.example.front.ui.screens.LoginScreen
import com.example.front.ui.screens.ProfileScreen
import com.example.front.viewmodel.SessionUiState
import com.example.front.viewmodel.SessionViewModel

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val sessionState by sessionViewModel.uiState.collectAsState()
    val navigateToLogin by sessionViewModel.navigateToLogin.collectAsState()
    val navController = rememberNavController()
    val isLoggedIn = (sessionState as? SessionUiState.Ready)?.isLoggedIn == true

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = AppRoute.Login.route
        ) {
            composable(AppRoute.Login.route) {
                LoginScreen(
                    onAuthenticated = { authResponse ->
                        val destination = if (authResponse.role == "ADMIN") {
                            AppRoute.AdminApplications.route
                        } else {
                            AppRoute.Applications.route
                        }
                        navController.navigate(destination) {
                            popUpTo(AppRoute.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.Applications.route) {
                ApplicationListScreen(
                    onCreateApplication = { navController.navigate(AppRoute.CreateApplication.route) },
                    onOpenApplication = {
                        navController.navigate(AppRoute.ApplicationDetail.createRoute(it))
                    },
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) }
                )
            }

            composable(AppRoute.AdminApplications.route) {
                AdminApplicationListScreen(
                    onOpenApplication = {
                        navController.navigate(AppRoute.AdminApplicationDetail.createRoute(it))
                    },
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) }
                )
            }

            composable(AppRoute.CreateApplication.route) {
                CreateApplicationScreen(
                    onBack = navController::popBackStack,
                    onSubmitted = { navController.popBackStack() }
                )
            }

            composable(AppRoute.Profile.route) {
                ProfileScreen(
                    onBack = navController::popBackStack,
                    onLogout = {
                        navController.navigate(AppRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = AppRoute.ApplicationDetail.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) {
                ApplicationDetailScreen(
                    onBack = navController::popBackStack,
                    onEditApplication = { applicationId ->
                        navController.navigate(AppRoute.EditApplication.createRoute(applicationId))
                    }
                )
            }

            composable(
                route = AppRoute.EditApplication.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) {
                EditApplicationScreen(
                    onBack = navController::popBackStack,
                    onSubmitted = {
                        navController.popBackStack(AppRoute.Applications.route, inclusive = false)
                    }
                )
            }

            composable(
                route = AppRoute.AdminApplicationDetail.route,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
            ) {
                AdminApplicationDetailScreen(
                    onBack = navController::popBackStack
                )
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val role = (sessionState as? SessionUiState.Ready)?.role ?: "USER"
            val destination = if (role == "ADMIN") {
                AppRoute.AdminApplications.route
            } else {
                AppRoute.Applications.route
            }
            if (navController.currentDestination?.route != destination) {
                navController.navigate(destination) {
                    popUpTo(AppRoute.Login.route) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(AppRoute.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            sessionViewModel.onNavigateToLoginHandled()
        }
    }
}

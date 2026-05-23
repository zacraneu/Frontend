package com.example.front.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.ui.screens.ApplicationDetailScreen
import com.example.front.ui.screens.ApplicationListScreen
import com.example.front.ui.screens.CreateApplicationScreen
import com.example.front.ui.screens.LoginScreen
import com.example.front.ui.screens.ProfileScreen
import com.example.front.viewmodel.SessionViewModel

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val navigateToLogin by sessionViewModel.navigateToLogin.collectAsState()

    val startDestination = remember(sessionViewModel.isLoggedIn) {
        if (sessionViewModel.isLoggedIn) {
            AppRoute.Applications.route
        } else {
            AppRoute.Login.route
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

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen(
                onAuthenticated = {
                    navController.navigate(AppRoute.Applications.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Applications.route) {
            ApplicationListScreen(
                onCreateApplication = { navController.navigate(AppRoute.CreateApplication.route) },
                onOpenApplication = { navController.navigate(AppRoute.ApplicationDetail.createRoute(it)) },
                onOpenProfile = { navController.navigate(AppRoute.Profile.route) }
            )
        }

        composable(AppRoute.CreateApplication.route) {
            CreateApplicationScreen(onBack = navController::popBackStack)
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
        ) { backStackEntry ->
            ApplicationDetailScreen(
                applicationId = backStackEntry.arguments?.getString("applicationId").orEmpty(),
                onBack = navController::popBackStack
            )
        }
    }
}

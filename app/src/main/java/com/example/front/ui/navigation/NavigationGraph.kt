package com.example.front.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.front.ui.screens.ApplicationDetailScreen
import com.example.front.ui.screens.ApplicationListScreen
import com.example.front.ui.screens.CreateApplicationScreen
import com.example.front.ui.screens.LoginScreen
import com.example.front.ui.screens.ProfileScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route
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
            ProfileScreen(onBack = navController::popBackStack)
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

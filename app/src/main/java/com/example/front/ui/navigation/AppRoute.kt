package com.example.front.ui.navigation

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Applications : AppRoute("applications")
    data object AdminApplications : AppRoute("admin/applications")
    data object CreateApplication : AppRoute("applications/create")
    data object Profile : AppRoute("profile")
    data object ApplicationDetail : AppRoute("applications/{applicationId}") {
        fun createRoute(applicationId: String): String = "applications/$applicationId"
    }

    data object EditApplication : AppRoute("applications/{applicationId}/edit") {
        fun createRoute(applicationId: String): String = "applications/$applicationId/edit"
    }

    data object AdminApplicationDetail : AppRoute("admin/applications/{applicationId}") {
        fun createRoute(applicationId: String): String = "admin/applications/$applicationId"
    }
}

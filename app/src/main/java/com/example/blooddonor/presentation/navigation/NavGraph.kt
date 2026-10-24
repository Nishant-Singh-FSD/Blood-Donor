package com.example.blooddonor.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.blooddonor.presentation.auth.LoginScreen
import com.example.blooddonor.presentation.auth.RegisterScreen
import com.example.blooddonor.presentation.request.EmergencyScreen
import com.example.blooddonor.presentation.home.HomeScreen
import com.example.blooddonor.presentation.profile.ProfileScreen
import com.example.blooddonor.presentation.triage.AiTriageScreen

@Composable
fun BloodDonorNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Login Screen
        composable(route = "login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(route = "home") {
                        popUpTo(route = "login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(route = "signup")
                }
            )
        }

        // Signup / Register Screen
        composable(route = "signup") {
            RegisterScreen(
                onSignupSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Home / Dashboard Screen
        composable(route = "home") {
            HomeScreen(
                onEmergencyClick = {
                    navController.navigate(route = "emergency")
                },
                onProfileClick = {
                    navController.navigate(route = "profile")
                },
                onAiTriageClick = {
                    navController.navigate(route = "ai_triage")
                }
            )
        }

        // Emergency Broadcast Screen
        composable(route = "emergency") {
            EmergencyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // User Profile Screen
        composable(route = "profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    navController.navigate(route = "login") {
                        popUpTo(route = "home") { inclusive = true }
                    }
                }
            )
        }

        // AI Clinical Triage Assistant Screen
        composable(route = "ai_triage") {
            AiTriageScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
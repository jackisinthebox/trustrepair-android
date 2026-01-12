package com.trustrepair.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trustrepair.app.navigation.Screen
import com.trustrepair.app.ui.screens.*
import com.trustrepair.app.ui.screens.provider.*

@Composable
fun TrustRepairApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // ==================== ENTRY POINT ====================
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onClientClick = { navController.navigate(Screen.Chat.route) },
                onProviderClick = { navController.navigate(Screen.ProviderLogin.route) }
            )
        }

        // ==================== CLIENT FLOW ====================
        composable(Screen.Chat.route) { backStackEntry ->
            // Check if we returned from verification
            val isVerified = backStackEntry.savedStateHandle.get<Boolean>("verified") ?: false

            ChatScreen(
                onVerifyClick = { navController.navigate(Screen.Verify.route) },
                onQuotesClick = { navController.navigate(Screen.Quotes.route) },
                isVerified = isVerified,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Verify.route) {
            VerifyScreen(
                onBack = { navController.popBackStack() },
                onVerified = {
                    // Set verified state and pop back to Chat
                    navController.previousBackStackEntry?.savedStateHandle?.set("verified", true)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Quotes.route) {
            QuotesScreen(
                onBack = { navController.popBackStack() },
                onQuoteSelected = { navController.navigate(Screen.Payment.route) }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate(Screen.Processing.route) }
            )
        }

        composable(Screen.Processing.route) {
            ProcessingScreen(
                onComplete = {
                    navController.navigate(Screen.Success.route) {
                        popUpTo(Screen.Chat.route)
                    }
                }
            )
        }

        composable(Screen.Success.route) {
            SuccessScreen(
                onContinue = { navController.navigate(Screen.Tracking.route) }
            )
        }

        composable(Screen.Tracking.route) {
            TrackingScreen(
                onBack = { navController.popBackStack() },
                onRate = { navController.navigate(Screen.Rating.route) }
            )
        }

        composable(Screen.Rating.route) {
            RatingScreen(
                onBack = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== PROVIDER FLOW ====================
        composable(Screen.ProviderLogin.route) {
            ProviderLoginScreen(
                onBack = { navController.popBackStack() },
                onLogin = {
                    navController.navigate(Screen.ProviderDashboard.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }

        composable(Screen.ProviderDashboard.route) {
            ProviderDashboardScreen(
                onJobRequestClick = { jobId ->
                    navController.navigate(Screen.JobRequest.createRoute(jobId))
                },
                onActiveJobClick = { jobId ->
                    navController.navigate(Screen.JobDetail.createRoute(jobId))
                },
                onActiveJobsTab = { navController.navigate(Screen.Calendar.route) },
                onEarningsTab = { navController.navigate(Screen.Earnings.route) },
                onProfileTab = { navController.navigate(Screen.ProviderProfile.route) },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.JobRequest.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobRequestScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onDecline = { navController.popBackStack() },
                onSendQuote = {
                    navController.navigate(Screen.QuoteBuilder.createRoute(jobId))
                }
            )
        }

        composable(
            route = Screen.QuoteBuilder.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            QuoteBuilderScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(Screen.ProviderDashboard.route) {
                        popUpTo(Screen.ProviderDashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ActiveJobs.route) {
            ActiveJobsScreen(
                onBack = { navController.popBackStack() },
                onJobClick = { jobId ->
                    navController.navigate(Screen.JobDetail.createRoute(jobId))
                }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onJobClick = { jobId ->
                    navController.navigate(Screen.JobDetail.createRoute(jobId))
                },
                onDashboardTab = { navController.navigate(Screen.ProviderDashboard.route) },
                onEarningsTab = { navController.navigate(Screen.Earnings.route) },
                onProfileTab = { navController.navigate(Screen.ProviderProfile.route) }
            )
        }

        composable(
            route = Screen.JobDetail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onSendQuote = { id ->
                    navController.navigate(Screen.QuoteBuilder.createRoute(id))
                },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.Earnings.route) {
            EarningsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProviderProfile.route) {
            ProviderProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

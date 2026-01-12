package com.trustrepair.app.navigation

/**
 * Navigation routes for TrustRepair app
 */
sealed class Screen(val route: String) {
    // Entry point
    data object Welcome : Screen("welcome")
    
    // Client screens
    data object Chat : Screen("client/chat")
    data object Verify : Screen("client/verify")
    data object Quotes : Screen("client/quotes")
    data object Payment : Screen("client/payment")
    data object Processing : Screen("client/processing")
    data object Success : Screen("client/success")
    data object Tracking : Screen("client/tracking")
    data object Rating : Screen("client/rating")
    
    // Provider screens
    data object ProviderLogin : Screen("provider/login")
    data object ProviderDashboard : Screen("provider/dashboard")
    data object JobRequest : Screen("provider/job-request/{jobId}") {
        fun createRoute(jobId: String) = "provider/job-request/$jobId"
    }
    data object QuoteBuilder : Screen("provider/quote-builder/{jobId}") {
        fun createRoute(jobId: String) = "provider/quote-builder/$jobId"
    }
    data object ActiveJobs : Screen("provider/active-jobs")
    data object Calendar : Screen("provider/calendar")
    data object JobDetail : Screen("provider/job-detail/{jobId}") {
        fun createRoute(jobId: String) = "provider/job-detail/$jobId"
    }
    data object Earnings : Screen("provider/earnings")
    data object ProviderProfile : Screen("provider/profile")
}

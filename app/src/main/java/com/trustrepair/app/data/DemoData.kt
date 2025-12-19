package com.trustrepair.app.data

import com.trustrepair.app.ui.theme.ProviderBlue
import com.trustrepair.app.ui.theme.ProviderGreen
import com.trustrepair.app.ui.theme.ProviderPurple
import androidx.compose.ui.graphics.Color

/**
 * Demo data for TrustRepair prototype
 * All data is static - no backend required
 */

// ==================== COMMON DATA CLASSES ====================

data class Provider(
    val id: Int,
    val name: String,
    val initials: String,
    val rating: Float,
    val reviewCount: Int,
    val yearsExperience: Int,
    val distanceKm: Float,
    val verified: Boolean = true,
    val avatarColor: Color
)

data class Quote(
    val id: Int,
    val provider: Provider,
    val price: Int,
    val isFixed: Boolean,
    val date: String,
    val timeSlot: String,
    val badge: QuoteBadge? = null
)

enum class QuoteBadge {
    BEST_VALUE,
    AVAILABLE_TODAY
}

data class Job(
    val type: String,
    val description: String,
    val location: String,
    val postcode: String
)

data class Booking(
    val job: Job,
    val quote: Quote,
    val accessCode: String,
    val accessFloor: String,
    val accessNotes: String
)

data class PriceBreakdown(
    val labor: Int,
    val parts: Int
) {
    val total: Int get() = labor + parts
}

// ==================== CLIENT-SIDE DATA ====================

val demoProviders = listOf(
    Provider(
        id = 1,
        name = "Karim D.",
        initials = "KD",
        rating = 4.9f,
        reviewCount = 21,
        yearsExperience = 8,
        distanceKm = 2.3f,
        avatarColor = ProviderGreen
    ),
    Provider(
        id = 2,
        name = "Marc L.",
        initials = "ML",
        rating = 4.7f,
        reviewCount = 15,
        yearsExperience = 5,
        distanceKm = 4.1f,
        avatarColor = ProviderPurple
    ),
    Provider(
        id = 3,
        name = "Sophie B.",
        initials = "SB",
        rating = 4.8f,
        reviewCount = 32,
        yearsExperience = 12,
        distanceKm = 5.8f,
        avatarColor = ProviderBlue
    )
)

val demoQuotes = listOf(
    Quote(
        id = 1,
        provider = demoProviders[0],
        price = 100,
        isFixed = true,
        date = "Lundi 20 janvier",
        timeSlot = "14h - 17h",
        badge = QuoteBadge.BEST_VALUE
    ),
    Quote(
        id = 2,
        provider = demoProviders[1],
        price = 120,
        isFixed = true,
        date = "Aujourd'hui",
        timeSlot = "16h - 19h",
        badge = QuoteBadge.AVAILABLE_TODAY
    ),
    Quote(
        id = 3,
        provider = demoProviders[2],
        price = 95,
        isFixed = false,
        date = "Mardi 21 janvier",
        timeSlot = "9h - 12h",
        badge = null
    )
)

val demoJob = Job(
    type = "Plomberie",
    description = "Fuite sous évier — raccord siphon",
    location = "Versailles",
    postcode = "78000"
)

val demoBooking = Booking(
    job = demoJob,
    quote = demoQuotes[0],
    accessCode = "4521B",
    accessFloor = "3ème étage, porte droite",
    accessNotes = "Interphone \"Dupont\""
)

const val demoPhoneNumber = "+33 6 12 34 56 78"

val demoPriceBreakdown = PriceBreakdown(
    labor = 80,
    parts = 20
)

const val demoPaymentReference = "PAY250115123456"

// ==================== PROVIDER-SIDE DATA ====================

// Logged-in provider (Karim's perspective)
data class LoggedInProvider(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val businessName: String,
    val specialty: String,
    val yearsExperience: Int,
    val rating: Float,
    val reviewCount: Int,
    val zone: String,
    val avatarColor: Color
)

val currentProvider = LoggedInProvider(
    id = 1,
    firstName = "Karim",
    lastName = "Dubois",
    businessName = "Plomberie Dubois",
    specialty = "Plomberie",
    yearsExperience = 8,
    rating = 4.9f,
    reviewCount = 21,
    zone = "Versailles et environs (15km)",
    avatarColor = ProviderGreen
)

// Dashboard stats
data class ProviderStats(
    val earnedThisMonth: Int,
    val pendingAmount: Int,
    val jobsCompleted: Int,
    val averageRating: Float
)

val demoProviderStats = ProviderStats(
    earnedThisMonth = 2450,
    pendingAmount = 180,
    jobsCompleted = 12,
    averageRating = 4.9f
)

// Client data (from provider's perspective)
data class Client(
    val id: Int,
    val name: String,
    val initials: String,
    val memberSince: String,
    val verified: Boolean,
    val phone: String
)

val demoClients = listOf(
    Client(
        id = 1,
        name = "Marie D.",
        initials = "MD",
        memberSince = "2023",
        verified = true,
        phone = "+33 6 12 34 56 78"
    ),
    Client(
        id = 2,
        name = "Pierre M.",
        initials = "PM",
        memberSince = "2022",
        verified = true,
        phone = "+33 6 98 76 54 32"
    ),
    Client(
        id = 3,
        name = "Sophie L.",
        initials = "SL",
        memberSince = "2024",
        verified = true,
        phone = "+33 6 11 22 33 44"
    )
)

// Job requests (incoming leads)
data class JobRequest(
    val id: String,
    val client: Client,
    val jobType: String,
    val description: String,
    val photos: List<String>, // URLs or resource IDs
    val location: String,
    val distanceKm: Float,
    val urgency: String,
    val availability: String,
    val accessCode: String,
    val accessNotes: String,
    val receivedAgo: String,
    val expiresIn: String
)

val demoJobRequests = listOf(
    JobRequest(
        id = "req1",
        client = demoClients[0],
        jobType = "Plomberie",
        description = "Fuite sous évier, le joint du siphon semble abîmé",
        photos = emptyList(),
        location = "Versailles",
        distanceKm = 2.3f,
        urgency = "Dès que possible",
        availability = "En semaine, après-midi",
        accessCode = "4521B",
        accessNotes = "3ème étage, interphone Dupont",
        receivedAgo = "15 min",
        expiresIn = "2h"
    ),
    JobRequest(
        id = "req2",
        client = demoClients[1],
        jobType = "Plomberie",
        description = "Chauffe-eau qui fuit par le bas",
        photos = emptyList(),
        location = "Le Chesnay",
        distanceKm = 4.1f,
        urgency = "Cette semaine",
        availability = "Flexible",
        accessCode = "",
        accessNotes = "Interphone MARTIN",
        receivedAgo = "45 min",
        expiresIn = "1h15"
    )
)

// Active jobs
enum class JobStatus {
    CONFIRMED,
    EN_ROUTE,
    IN_PROGRESS,
    COMPLETED
}

data class ActiveJob(
    val id: String,
    val client: Client,
    val jobType: String,
    val description: String,
    val date: String,
    val timeSlot: String,
    val status: JobStatus,
    val address: String,
    val accessCode: String,
    val accessNotes: String,
    val priceBreakdown: PriceBreakdown,
    val isFixed: Boolean
)

val demoActiveJobs = listOf(
    ActiveJob(
        id = "job1",
        client = demoClients[2],
        jobType = "Plomberie",
        description = "Remplacement robinet cuisine",
        date = "Lundi 20 janvier",
        timeSlot = "14h - 17h",
        status = JobStatus.CONFIRMED,
        address = "15 rue de la Paix, 78000 Versailles",
        accessCode = "1234",
        accessNotes = "2ème étage gauche",
        priceBreakdown = PriceBreakdown(labor = 100, parts = 50),
        isFixed = true
    )
)

// Transaction history
data class Transaction(
    val id: String,
    val clientName: String,
    val jobType: String,
    val amount: Int,
    val date: String,
    val isPaid: Boolean
)

val demoTransactions = listOf(
    Transaction("tx1", "Thomas R.", "Débouchage", 120, "15 jan", true),
    Transaction("tx2", "Claire B.", "Fuite WC", 95, "12 jan", true),
    Transaction("tx3", "Marc V.", "Installation", 280, "10 jan", true),
    Transaction("tx4", "Anne S.", "Robinet", 150, "8 jan", true),
    Transaction("tx5", "Sophie L.", "Robinet cuisine", 150, "En cours", false)
)

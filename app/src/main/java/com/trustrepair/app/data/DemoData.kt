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

// Job pipeline statuses (full lifecycle)
enum class JobStatus {
    PENDING_QUOTE,    // New request, no quote sent yet
    QUOTE_SENT,       // Quote sent, awaiting client response
    QUOTE_ACCEPTED,   // Client accepted, needs scheduling confirmation
    CONFIRMED,        // Scheduled and confirmed
    EN_ROUTE,         // Provider is on the way
    IN_PROGRESS,      // Work is being done
    COMPLETED         // Work finished
}

data class ActiveJob(
    val id: String,
    val client: Client,
    val jobType: String,
    val description: String,
    val date: String,              // Empty or "À planifier" for early stages
    val timeSlot: String,          // Empty for early stages
    val status: JobStatus,
    val address: String,
    val accessCode: String,
    val accessNotes: String,
    val priceBreakdown: PriceBreakdown?,  // Null if no quote yet
    val isFixed: Boolean?,                 // Null if no quote yet
    val urgency: String = "",              // For early stages
    val availability: String = "",         // For early stages
    val receivedAgo: String = "",          // For display "Il y a X"
    val expiresIn: String = ""             // For pending quotes
)

// All jobs in the pipeline (unified view)
val demoActiveJobs = listOf(
    // Stage 1: PENDING_QUOTE - New requests needing quotes
    ActiveJob(
        id = "job1",
        client = demoClients[0],
        jobType = "Plomberie",
        description = "Fuite sous évier, le joint du siphon semble abîmé",
        date = "",
        timeSlot = "",
        status = JobStatus.PENDING_QUOTE,
        address = "12 rue des Lilas, 78000 Versailles",
        accessCode = "4521B",
        accessNotes = "3ème étage, interphone Dupont",
        priceBreakdown = null,
        isFixed = null,
        urgency = "Dès que possible",
        availability = "En semaine, après-midi",
        receivedAgo = "15 min",
        expiresIn = "2h"
    ),
    ActiveJob(
        id = "job2",
        client = demoClients[1],
        jobType = "Plomberie",
        description = "Chauffe-eau qui fuit par le bas",
        date = "",
        timeSlot = "",
        status = JobStatus.PENDING_QUOTE,
        address = "8 avenue Jean Jaurès, 78150 Le Chesnay",
        accessCode = "",
        accessNotes = "Interphone MARTIN",
        priceBreakdown = null,
        isFixed = null,
        urgency = "Cette semaine",
        availability = "Flexible",
        receivedAgo = "45 min",
        expiresIn = "1h15"
    ),
    // Stage 2: QUOTE_SENT - Awaiting client response
    ActiveJob(
        id = "job3",
        client = Client(4, "Thomas R.", "TR", "2023", true, "+33 6 55 44 33 22"),
        jobType = "Plomberie",
        description = "Débouchage canalisation cuisine",
        date = "Mercredi 22 janvier",
        timeSlot = "9h - 12h",
        status = JobStatus.QUOTE_SENT,
        address = "25 rue Victor Hugo, 78000 Versailles",
        accessCode = "1234",
        accessNotes = "RDC gauche",
        priceBreakdown = PriceBreakdown(labor = 90, parts = 30),
        isFixed = true,
        receivedAgo = "2h"
    ),
    // Stage 3: QUOTE_ACCEPTED - Client accepted, needs confirmation
    ActiveJob(
        id = "job4",
        client = Client(5, "Claire B.", "CB", "2022", true, "+33 6 77 88 99 00"),
        jobType = "Plomberie",
        description = "Remplacement joint chasse d'eau",
        date = "Jeudi 23 janvier",
        timeSlot = "14h - 17h",
        status = JobStatus.QUOTE_ACCEPTED,
        address = "3 place du Marché, 78000 Versailles",
        accessCode = "5678",
        accessNotes = "2ème étage, porte bleue",
        priceBreakdown = PriceBreakdown(labor = 60, parts = 25),
        isFixed = true,
        receivedAgo = "1 jour"
    ),
    // Stage 4: CONFIRMED - Scheduled
    ActiveJob(
        id = "job5",
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
    ),
    // Stage 5: IN_PROGRESS - Currently working
    ActiveJob(
        id = "job6",
        client = Client(6, "Marc V.", "MV", "2021", true, "+33 6 11 22 33 44"),
        jobType = "Plomberie",
        description = "Installation mitigeur salle de bain",
        date = "Aujourd'hui",
        timeSlot = "10h - 13h",
        status = JobStatus.IN_PROGRESS,
        address = "42 boulevard de la Reine, 78000 Versailles",
        accessCode = "9999",
        accessNotes = "4ème étage avec ascenseur",
        priceBreakdown = PriceBreakdown(labor = 120, parts = 80),
        isFixed = true
    ),
    // Stage 6: COMPLETED - Finished jobs
    ActiveJob(
        id = "job7",
        client = Client(7, "Anne S.", "AS", "2023", true, "+33 6 99 88 77 66"),
        jobType = "Plomberie",
        description = "Réparation fuite WC",
        date = "Vendredi 17 janvier",
        timeSlot = "9h - 12h",
        status = JobStatus.COMPLETED,
        address = "18 rue Royale, 78000 Versailles",
        accessCode = "2222",
        accessNotes = "1er étage",
        priceBreakdown = PriceBreakdown(labor = 70, parts = 25),
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

// Quote templates for quick quote creation
data class QuoteTemplate(
    val id: String,
    val name: String,
    val lineItems: List<Pair<String, Int>> // description, amount
)

val demoQuoteTemplates = listOf(
    QuoteTemplate("t1", "Débouchage standard", listOf("Main d'œuvre" to 80, "Déplacement" to 30)),
    QuoteTemplate("t2", "Fuite simple", listOf("Main d'œuvre" to 60, "Diagnostic" to 40, "Joint" to 15)),
    QuoteTemplate("t3", "Installation robinet", listOf("Main d'œuvre" to 100, "Déplacement" to 30, "Pièces" to 50))
)

// Provider notifications
data class ProviderNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val isRead: Boolean
)

enum class NotificationType {
    NEW_REQUEST,      // ProviderPurple
    QUOTE_ACCEPTED,   // SuccessGreen
    PAYMENT_RECEIVED, // SuccessGreen
    REMINDER,         // WarningAmber
    SYSTEM            // Gray
}

val demoNotifications = listOf(
    ProviderNotification("n1", NotificationType.NEW_REQUEST, "Nouvelle demande", "Marie D. - Plomberie à Versailles", "Il y a 15 min", false),
    ProviderNotification("n2", NotificationType.QUOTE_ACCEPTED, "Devis accepté !", "Thomas R. a accepté votre devis de 120 €", "Il y a 2h", false),
    ProviderNotification("n3", NotificationType.PAYMENT_RECEIVED, "Paiement reçu", "150 € crédités sur votre compte", "Hier", true),
    ProviderNotification("n4", NotificationType.REMINDER, "Rappel RDV demain", "Sophie L. - Lundi 14h-17h", "Hier", true),
    ProviderNotification("n5", NotificationType.SYSTEM, "Mise à jour disponible", "Nouvelle version 1.1.0", "Il y a 3 jours", true)
)

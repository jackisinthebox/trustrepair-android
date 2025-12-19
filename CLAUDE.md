# TrustRepair Android Prototype

## Project Overview

TrustRepair is a French home services marketplace connecting homeowners with verified tradespeople. This is a **frontend prototype** for user testing and feedback collection — no backend required.

**Two User Flows:**
- **Client (Marie)** — Homeowner seeking repairs
- **Provider (Jean)** — Artisan offering services

**Tech Stack:** Kotlin + Jetpack Compose (Material 3)
**Min SDK:** 26 (Android 8.0)
**Target SDK:** 34 (Android 14)

---

## Architecture

```
app/src/main/
├── java/com/trustrepair/app/
│   ├── MainActivity.kt              # Single activity, hosts Compose
│   ├── TrustRepairApp.kt            # Root composable with navigation
│   ├── navigation/
│   │   └── NavGraph.kt              # Navigation routes and graph
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Color.kt             # Brand colors
│   │   │   ├── Type.kt              # Typography scale
│   │   │   └── Theme.kt             # Material theme
│   │   ├── components/              # Reusable UI components
│   │   │   ├── Buttons.kt
│   │   │   ├── Cards.kt
│   │   │   ├── ChatBubble.kt
│   │   │   ├── Headers.kt
│   │   │   ├── Inputs.kt
│   │   │   └── ...
│   │   └── screens/
│   │       ├── client/              # Client screens
│   │       │   ├── WelcomeScreen.kt
│   │       │   ├── ChatScreen.kt
│   │       │   ├── VerifyScreen.kt
│   │       │   ├── QuotesScreen.kt
│   │       │   ├── PaymentScreen.kt
│   │       │   ├── ProcessingScreen.kt
│   │       │   ├── SuccessScreen.kt
│   │       │   ├── TrackingScreen.kt
│   │       │   └── RatingScreen.kt
│   │       └── provider/            # Provider screens
│   │           ├── ProviderLoginScreen.kt
│   │           ├── ProviderDashboardScreen.kt
│   │           ├── JobRequestScreen.kt
│   │           ├── QuoteBuilderScreen.kt
│   │           ├── ActiveJobsScreen.kt
│   │           ├── JobDetailScreen.kt
│   │           └── EarningsScreen.kt
│   └── data/
│       ├── DemoData.kt              # Static demo data
│       └── ChatFlow.kt              # Scripted conversation
└── res/
    ├── values/
    │   └── strings.xml              # All French strings
    └── drawable/                    # Icons and images
```

---

## Design System

### Colors (use these exact values)

```kotlin
// Primary
val TrustBlue = Color(0xFF2563EB)
val TrustBlueDark = Color(0xFF1D4ED8)
val TrustBlueLight = Color(0xFFDBEAFE)

// Status
val SuccessGreen = Color(0xFF059669)
val SuccessGreenLight = Color(0xFFD1FAE5)
val WarningAmber = Color(0xFFD97706)
val WarningAmberLight = Color(0xFFFEF3C7)
val ErrorRed = Color(0xFFDC2626)
val ErrorRedLight = Color(0xFFFEE2E2)

// Neutrals
val Gray900 = Color(0xFF111827)
val Gray700 = Color(0xFF374151)
val Gray500 = Color(0xFF6B7280)
val Gray400 = Color(0xFF9CA3AF)
val Gray300 = Color(0xFFD1D5DB)
val Gray200 = Color(0xFFE5E7EB)
val Gray100 = Color(0xFFF3F4F6)
val Gray50 = Color(0xFFF9FAFB)
```

### Typography

- Font: System default (Roboto on Android)
- Headings: SemiBold/Bold
- Body: Regular
- Minimum text size: 14sp
- Minimum touch target: 48dp

### Spacing

Base unit: 4dp. Use multiples: 4, 8, 12, 16, 20, 24, 32, 48, 64.

### Corner Radius

- Small (inputs, chips): 8dp
- Medium (buttons, small cards): 12dp
- Large (cards, sheets): 16dp
- XL (modals): 24dp

---

## Screens to Build

### ROLE SELECTION

### 1. Welcome Screen (Role Selector)
- Blue gradient background (#2563EB → #1E40AF)
- App logo and name "TrustRepair"
- Tagline: "La confiance au service de votre maison"
- **Two role buttons (equal prominence):**
  - "Je cherche un artisan" → Client flow
  - "Je suis artisan" → Provider flow
- Small text link at bottom: "Comment ça marche ?"

---

## CLIENT SCREENS (Marie)

### 2. Chat Screen
- Header with avatar, "TrustRepair", online status
- Scrollable message list
- AI messages: white bubble, left-aligned
- User messages: blue bubble, right-aligned
- Quick reply chips below AI messages
- Bottom input bar with camera button and send

**Chat Flow (scripted):**
1. AI: "Bonjour ! 👋 Quel problème rencontrez-vous ?" → Quick replies: [Plomberie, Électricité, Serrurerie]
2. User selects → AI: "Où se situe la fuite ?" → Quick replies: [Sous l'évier, WC, Chauffe-eau]
3. User selects → AI: "Avez-vous une photo ?" → Quick replies: [📸 Photo, Pas de photo]
4. User selects → AI: "Quel est votre code postal ?" → Free text input
5. User types → AI: "Pour recevoir les devis, vérifiez votre numéro" → [📱 Vérifier]
6. After verify → AI: "3 artisans disponibles !" → Show quote card with "Comparer les devis" button

### 3. Verify Screen (OTP)
- Back button
- Phone icon
- Title: "Vérifiez votre numéro"
- Subtitle with phone number (+33 6 XX XX XX XX)
- 6 individual digit inputs
- Auto-advance on input
- Resend link
- Verify button

### 4. Quotes Screen
- Header: "Comparer les devis" with back button
- Info banner: "Écart de prix de 20%..."
- 3 Quote cards, each with:
  - Badge (optional): "Meilleur rapport qualité-prix" or "Disponible aujourd'hui"
  - Provider avatar with initials
  - Name, rating (stars), review count
  - Price (large)
  - Details: date, experience, distance
  - Two buttons: "Détails" (outline), "Choisir" (primary)

### 5. Payment Screen
- Header: "Récapitulatif" (Étape 1 sur 2)
- Security badge: "Paiement sécurisé par Mangopay"
- Booking card: provider, job, date, location
- Price breakdown: labor + parts = total
- Guarantee cards (escrow, refund, data protection)
- Fixed bottom: "Payer X € de manière sécurisée"

### 6. Processing Screen
- Centered spinner animation
- Credit card icon in center
- "Traitement en cours..."
- Step indicators: ✓ Carte vérifiée, ⟳ Autorisation, ○ Confirmation
- Auto-navigate to Success after 3s

### 7. Success Screen
- Green checkmark with bounce animation
- "Paiement confirmé !"
- Confirmation card (green background)
- Booking summary (compact)
- Next steps timeline (4 steps)
- Escrow reminder (blue box)
- Bottom CTA: "Suivre ma réservation"

### 8. Tracking Screen
- Status card: calendar icon, "Réservation confirmée", date
- Provider card: avatar, name, rating, Message/Call buttons
- Timeline: current step highlighted, completed steps checked

### 10. Rating Screen
- Provider avatar
- "Comment s'est passée l'intervention ?"
- 5 star buttons (tap to rate)
- Rating label (Très satisfait, etc.)
- Optional comment textarea
- Submit button

---

## PROVIDER SCREENS (Jean)

Provider screens use **Purple accent (#7C3AED)** instead of blue to differentiate the experience.

### 11. Provider Login Screen
- TrustRepair logo (smaller, top)
- "Espace Artisan" title
- Email input field
- Password input field (with show/hide toggle)
- "Se connecter" primary button
- "Mot de passe oublié ?" link
- Divider with "ou"
- "Créer un compte" secondary button
- **Demo:** Any email/password works

### 12. Provider Dashboard Screen
- **Header:** Provider avatar, "Bonjour, Karim 👋", notification bell
- **Stats row (3 cards):**
  - "Ce mois" — 2 450 € earned
  - "En attente" — 180 € pending
  - "Note" — 4.9 ★
- **Section: "Nouvelles demandes" (2)**
  - Job request cards (compact):
    - Job type icon + title
    - Location + distance
    - "Il y a 15 min"
    - → Tap opens JobRequestScreen
- **Section: "Travaux en cours" (1)**
  - Active job card:
    - Client name (Marie D.)
    - Job type + date
    - Status badge: "Confirmé"
    - → Tap opens JobDetailScreen
- **Bottom navigation:**
  - Demandes (home icon, selected)
  - Travaux (briefcase icon)
  - Revenus (wallet icon)
  - Profil (user icon)

### 13. Job Request Screen (New Lead)
- **Header:** Back button, "Nouvelle demande", timer badge "Expire dans 2h"
- **Client info card:**
  - Avatar with initial, "Marie D."
  - Member since, verification badge
  - Location: "Versailles (2.3 km)"
- **Job details card:**
  - Type: "Plomberie"
  - Description: "Fuite sous évier, le joint du siphon semble abîmé"
  - Photos (horizontal scroll, tappable to enlarge)
  - Urgency: "Dès que possible"
  - Availability: "En semaine, après-midi"
- **Access info:**
  - "Accès: Code 4521B, 3ème étage"
- **Bottom actions (sticky):**
  - "Refuser" (outline button, left)
  - "Envoyer un devis" (primary button, right) → QuoteBuilderScreen

### 14. Quote Builder Screen
- **Header:** Back button, "Créer un devis"
- **Job summary (compact):** Marie D. — Plomberie — Versailles
- **Line items section:**
  - Default item: "Main d'œuvre" — editable amount (€)
  - "+ Ajouter une ligne" button
  - Each line: description input + amount input + delete button
- **Common items (quick add chips):**
  - "Déplacement", "Pièces", "Diagnostic", "Urgence"
- **Date/time picker:**
  - Calendar date selector
  - Time slot selector (matin/après-midi/soir)
- **Price type toggle:**
  - "Prix fixe" (default) / "Estimation"
- **Total display:** Large, updates live
- **Optional message:** Textarea for notes to client
- **Bottom (sticky):**
  - "Aperçu" (outline) — shows quote preview modal
  - "Envoyer le devis" (primary)

### 15. Active Jobs Screen (Tab 2)
- **Header:** "Mes travaux"
- **Filter chips:** Tous, Aujourd'hui, Cette semaine, À venir
- **Job cards list:**
  - Each card:
    - Status badge (Confirmé/En route/En cours/Terminé)
    - Client name + avatar
    - Job type + short description
    - Date + time slot
    - Address (truncated)
    - → Tap opens JobDetailScreen
- **Empty state:** "Aucun travail en cours" + illustration

### 16. Job Detail Screen
- **Header:** Back button, "Détail du travail", overflow menu (⋮)
- **Status banner:**
  - Color-coded by status
  - "Confirmé — Lundi 20 janvier, 14h-17h"
- **Client card:**
  - Avatar, name, phone, message button, call button
- **Job info card:**
  - Type + description
  - Photos (if any)
  - Special instructions
- **Access card:**
  - Address (full, tappable for maps)
  - Access code
  - Floor/door info
  - Notes
- **Price card:**
  - Breakdown: labor + parts
  - Total
  - "Prix fixe" or "Estimation" badge
- **Action buttons (context-dependent):**
  - If Confirmed: "Je suis en route" → changes status
  - If En route: "Je suis arrivé" → changes status
  - If En cours: "Travail terminé" → shows completion flow
- **Completion flow (modal):**
  - "Le travail est-il terminé comme prévu ?"
  - "Oui, conforme au devis" → Success state
  - "Non, il y a eu des modifications" → Adjustment form

### 17. Earnings Screen (Tab 3)
- **Header:** "Mes revenus"
- **Balance card (prominent):**
  - "Solde disponible"
  - Large amount: "2 450 €"
  - "Retirer" primary button
- **Pending card:**
  - "En attente de validation"
  - Amount: "180 €"
  - Info icon with tooltip: "Libéré après confirmation client"
- **Period selector:** Cette semaine / Ce mois / Cette année
- **Stats row:**
  - Travaux terminés: 12
  - Revenus: 2 450 €
  - Note moyenne: 4.9 ★
- **Transaction history:**
  - List of payments:
    - Client name
    - Job type
    - Amount
    - Date
    - Status badge (Payé/En attente)
  - Tap → detail modal

### 18. Provider Profile Screen (Tab 4)
- **Header:** "Mon profil"
- **Profile card:**
  - Large avatar (editable)
  - Name: "Karim Dubois"
  - "Plombier · 8 ans d'exp."
  - Rating: ★ 4.9 (21 avis)
  - "Voir mon profil public"
- **Menu items:**
  - Informations personnelles →
  - Documents et certifications →
  - Zone d'intervention →
  - Disponibilités →
  - Paramètres de notification →
  - Aide et support →
- **Bottom:**
  - "Se déconnecter" (destructive text button)
  - App version

---

## Navigation Flow

```
                                    ┌─────────────────────────────────────────────────────────┐
                                    │                      WELCOME                             │
                                    │            "Je cherche un artisan"  |  "Je suis artisan" │
                                    └─────────────────┬───────────────────────────┬───────────┘
                                                      │                           │
                              ┌───────────────────────▼───────────────────┐       │
                              │              CLIENT FLOW                   │       │
                              │                                            │       │
                              │  Chat → Verify → Chat → Quotes → Payment  │       │
                              │                           │                │       │
                              │                      Processing            │       │
                              │                           │                │       │
                              │                       Success              │       │
                              │                           │                │       │
                              │                      Tracking → Rating     │       │
                              └────────────────────────────────────────────┘       │
                                                                                   │
                              ┌────────────────────────────────────────────────────▼──┐
                              │                    PROVIDER FLOW                       │
                              │                                                        │
                              │  Login → Dashboard ←──────────────────────┐           │
                              │              │                             │           │
                              │    ┌─────────┼─────────┬───────────────┐   │           │
                              │    ▼         ▼         ▼               ▼   │           │
                              │ JobRequest  ActiveJobs  Earnings   Profile │           │
                              │    │            │                          │           │
                              │    ▼            ▼                          │           │
                              │ QuoteBuilder  JobDetail ───────────────────┘           │
                              └────────────────────────────────────────────────────────┘
```

### Client Routes
- `welcome`
- `client/chat`
- `client/verify`
- `client/quotes`
- `client/payment`
- `client/processing`
- `client/success`
- `client/tracking`
- `client/rating`

### Provider Routes
- `provider/login`
- `provider/dashboard`
- `provider/job-request/{jobId}`
- `provider/quote-builder/{jobId}`
- `provider/active-jobs`
- `provider/job-detail/{jobId}`
- `provider/earnings`
- `provider/profile`

---

## Demo Data

All data is hardcoded. Use these values:

### Client-Side Data

**Provider 1 (Best value):**
- Name: Karim D.
- Rating: 4.9 (21 avis)
- Price: 100 €
- Date: Lundi 20 janvier, 14h-17h
- Experience: 8 ans
- Distance: 2.3 km

**Provider 2 (Fast):**
- Name: Marc L.
- Rating: 4.7 (15 avis)
- Price: 120 €
- Date: Aujourd'hui, 16h-19h
- Experience: 5 ans
- Distance: 4.1 km

**Provider 3:**
- Name: Sophie B.
- Rating: 4.8 (32 avis)
- Price: 95 € (estimé)
- Date: Mardi 21 janvier, 9h-12h
- Experience: 12 ans
- Distance: 5.8 km

**Job:**
- Type: Plomberie
- Description: Fuite sous évier — raccord siphon
- Location: Versailles, 78000

### Provider-Side Data (Karim's View)

**Logged-in Provider:**
- Name: Karim Dubois
- Business: Plomberie Dubois
- Experience: 8 ans
- Rating: 4.9 (21 avis)
- Zone: Versailles et environs (15km)

**Dashboard Stats:**
- Earned this month: 2 450 €
- Pending: 180 €
- Jobs completed: 12
- Average rating: 4.9

**Incoming Job Requests (2):**

*Request 1:*
- Client: Marie D.
- Member since: 2023
- Verified: Yes
- Type: Plomberie
- Description: "Fuite sous évier, le joint du siphon semble abîmé"
- Location: Versailles (2.3 km)
- Urgency: Dès que possible
- Availability: En semaine, après-midi
- Access: Code 4521B, 3ème étage
- Received: Il y a 15 min
- Expires: Dans 2h

*Request 2:*
- Client: Pierre M.
- Member since: 2022
- Verified: Yes
- Type: Plomberie
- Description: "Chauffe-eau qui fuit par le bas"
- Location: Le Chesnay (4.1 km)
- Urgency: Cette semaine
- Availability: Flexible
- Access: Interphone MARTIN
- Received: Il y a 45 min
- Expires: Dans 1h15

**Active Job (1):**
- Client: Sophie L.
- Type: Plomberie
- Description: Remplacement robinet cuisine
- Date: Lundi 20 janvier, 14h-17h
- Status: Confirmé
- Address: 15 rue de la Paix, 78000 Versailles
- Access: Code 1234, 2ème étage gauche
- Price: 150 € (fixe)
  - Main d'œuvre: 100 €
  - Pièces: 50 €

**Recent Transactions:**

| Date | Client | Job | Amount | Status |
|------|--------|-----|--------|--------|
| 15 jan | Thomas R. | Débouchage | 120 € | Payé |
| 12 jan | Claire B. | Fuite WC | 95 € | Payé |
| 10 jan | Marc V. | Installation | 280 € | Payé |
| 8 jan | Anne S. | Robinet | 150 € | Payé |

---

## Code Style

- Use Kotlin idioms (scope functions, null safety, data classes)
- Compose: stateless composables with hoisted state
- Preview annotations for all screens and components
- Extract colors/dimensions to theme
- All user-facing strings in French
- Comments in English

---

## Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run on device
adb shell am start -n com.trustrepair.app/.MainActivity
```

---

## Important Notes

1. **This is a prototype** — no real backend, no real payments
2. **All interactions are simulated** — chat is scripted, OTP accepts any 6 digits
3. **Focus on visual fidelity** — must look professional and trustworthy
4. **French language only** — all UI text in French
5. **Touch targets** — minimum 48dp for all interactive elements
6. **Animations** — subtle but present (screen transitions, button feedback, loading states)

---

## Getting Started

1. Create new project in Android Studio: "Empty Compose Activity"
2. Package name: `com.trustrepair.app`
3. Minimum SDK: 26
4. Build configuration language: Kotlin DSL

Then ask Claude Code to implement each screen following this spec.

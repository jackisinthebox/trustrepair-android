# TrustRepair Android Prototype

Native Android prototype for the TrustRepair French home services platform.

## Two User Flows

This prototype includes both sides of the marketplace:

| Flow | User | Screens |
|------|------|---------|
| **Client** | Marie (homeowner) | Chat → Verify → Quotes → Payment → Tracking → Rating |
| **Provider** | Jean (artisan) | Login → Dashboard → Job Requests → Quote Builder → Active Jobs → Earnings |

## Quick Start

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog or newer)
- Android SDK 34
- JDK 17

### Setup

1. **Open in Android Studio**
   - File → Open → Select the `trustrepair-android` folder
   - Wait for Gradle sync to complete

2. **Connect your Android phone**
   - Enable Developer Options on your phone
   - Enable USB Debugging
   - Connect via USB

3. **Run**
   - Click the green "Run" button (or Shift+F10)
   - Select your device
   - App installs and launches

### Build APK

To generate an APK file you can share:

```bash
./gradlew assembleDebug
```

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/
├── java/com/trustrepair/app/
│   ├── MainActivity.kt           # Entry point
│   ├── TrustRepairApp.kt         # Navigation host
│   ├── navigation/
│   │   └── NavGraph.kt           # All routes (client + provider)
│   ├── ui/
│   │   ├── theme/                # Colors, typography, theme
│   │   └── screens/
│   │       ├── WelcomeScreen.kt  # Role selector
│   │       ├── ChatScreen.kt     # Client: Chat
│   │       ├── VerifyScreen.kt   # Client: OTP
│   │       ├── QuotesScreen.kt   # Client: Compare quotes
│   │       ├── PaymentScreen.kt  # Client: Payment
│   │       ├── ...
│   │       └── provider/         # Provider screens
│   │           ├── ProviderLoginScreen.kt
│   │           ├── ProviderDashboardScreen.kt
│   │           ├── JobRequestScreen.kt
│   │           ├── QuoteBuilderScreen.kt
│   │           └── ...
│   └── data/
│       └── DemoData.kt           # All demo data (client + provider)
└── res/
    └── values/
        └── strings.xml           # All French strings
```

## Screens

### Client Flow
| Screen | Status | Description |
|--------|--------|-------------|
| Welcome | ✅ Done | Role selector (Client/Provider) |
| Chat | 🔲 Placeholder | Conversational intake |
| Verify | 🔲 Placeholder | OTP phone verification |
| Quotes | 🔲 Placeholder | Provider comparison |
| Payment | 🔲 Placeholder | Payment summary |
| Processing | ✅ Basic | Loading animation |
| Success | 🔲 Placeholder | Confirmation |
| Tracking | 🔲 Placeholder | Job status |
| Rating | 🔲 Placeholder | Post-service feedback |

### Provider Flow
| Screen | Status | Description |
|--------|--------|-------------|
| Login | ✅ Done | Email/password (any works) |
| Dashboard | 🔲 Placeholder | Stats, requests, active jobs |
| Job Request | 🔲 Placeholder | View incoming lead |
| Quote Builder | 🔲 Placeholder | Create and send quote |
| Active Jobs | 🔲 Placeholder | List of current jobs |
| Job Detail | 🔲 Placeholder | Manage active job |
| Earnings | 🔲 Placeholder | Balance and history |
| Profile | 🔲 Placeholder | Settings and logout |

## Using Claude Code

To complete the screens, use Claude Code CLI:

```bash
cd trustrepair-android
claude
```

Then ask Claude to implement each screen:

```
Implement the ChatScreen following the CLAUDE.md specification
```

```
Implement the ProviderDashboardScreen with stats cards, 
new requests section, and bottom navigation
```

Claude Code will read `CLAUDE.md` for design specifications and implement the screens.

## Design System

**Client screens:** Blue accent (#2563EB)
**Provider screens:** Purple accent (#7C3AED)

- **Min Touch Target:** 48dp
- **Corner Radius:** 8-16dp
- **All text in French**

See `CLAUDE.md` for complete design specifications.

## Tech Stack

- Kotlin 1.9.20
- Jetpack Compose (Material 3)
- Compose Navigation
- Min SDK 26 (Android 8.0)
- Target SDK 34 (Android 14)

## Notes

- This is a **frontend prototype** — no backend
- All data is hardcoded in `DemoData.kt`
- OTP verification accepts any 6 digits
- Provider login accepts any email/password
- Payment flow is simulated (no real transactions)

## License

Proprietary - TrustRepair

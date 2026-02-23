# Golazo Medical - UEFA Medical Platform

A mobile-first medical platform for UEFA/football organizations built with Kotlin and Jetpack Compose.

## Architecture

- **UI**: Jetpack Compose with Material 3
- **Navigation**: Android Navigation Component with Compose
- **Networking**: Retrofit + OkHttp, Kotlin Serialization for JSON
- **State Management**: ViewModels + StateFlow + Compose state
- **Dependency Injection**: Hilt
- **Architecture Pattern**: MVVM with Repository pattern

## Design System: Golazo v4.0

- Primary color: `#1A4B8C` (UEFA Blue)
- Cards: 16dp rounded corners, subtle border, white background
- Font sizes: 10sp–18sp compact mobile range
- Bottom navigation: 5 tabs per role

## User Roles

### Player (5 tabs: Home, Injuries, Wellbeing, PCME, Consent)
- Dashboard with simulated performance metrics
- Injury reporting and tracking with RTP progression
- Wellbeing hub with bio signals, breathing exercises, session library
- Find Help: private psychologist directory and booking
- PCME records (read-only)
- Consent management with invitation tokens

### Doctor (5 tabs: Home, Players, Training, Playbook, Settings)
- Dashboard with player/injury/PCME overview
- Player roster management with invite capability
- Injury management across all players
- PCME form creation with prescription import
- Training session logging
- Medical playbook reference

### Shared
- Intelligence Platform: interactive graph visualization + AI chat
- Tactical Simulations

## API Configuration

The app connects to a REST API backend at `http://10.0.2.2:3000/` (Android emulator localhost).

To change the API URL, edit `AppModule.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
```

## Auth Flow

1. Login (email + password + role)
2. New user → Onboarding (profile setup) → Terms & Conditions
3. Returning user → PIN 2FA → Home

## Building

Open the project in Android Studio and sync Gradle. Requires:
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Kotlin 1.9.20

## Project Structure

```
app/src/main/java/com/golazo/medical/
├── GolazoApp.kt                    # Hilt Application
├── MainActivity.kt                 # Entry point
├── data/
│   ├── SimulatedData.kt           # Frontend-only simulated data
│   ├── api/GolazoApi.kt           # Retrofit API interface
│   ├── model/
│   │   ├── Models.kt              # Data models (User, PlayerProfile, etc.)
│   │   └── ApiResponses.kt        # Request/Response DTOs
│   └── repository/
│       ├── GolazoRepository.kt    # Repository layer
│       └── SessionManager.kt     # User session state
├── di/AppModule.kt                # Hilt DI module
├── navigation/
│   ├── Routes.kt                  # Route constants
│   └── GolazoNavHost.kt          # Navigation graph + bottom nav
├── ui/
│   ├── theme/Theme.kt            # Golazo design system
│   ├── components/CommonComponents.kt  # Shared UI components
│   ├── auth/                      # Login, PIN, Onboarding, Terms
│   ├── player/                    # All player screens
│   ├── doctor/                    # All doctor screens
│   └── shared/                    # Intelligence, Simulations
```

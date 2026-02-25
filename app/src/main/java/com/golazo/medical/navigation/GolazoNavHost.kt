package com.golazo.medical.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.golazo.medical.ui.auth.*
import com.golazo.medical.ui.doctor.*
import com.golazo.medical.ui.player.*
import com.golazo.medical.ui.shared.*
import com.golazo.medical.ui.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val playerNavItems = listOf(
    BottomNavItem(Routes.PLAYER_HOME, "Home", Icons.Default.Home),
    BottomNavItem(Routes.PLAYER_INJURIES, "Injuries", Icons.Default.LocalHospital),
    BottomNavItem(Routes.PLAYER_WELLBEING, "Wellbeing", Icons.Default.SelfImprovement),
    BottomNavItem(Routes.PLAYER_PCME, "PCME", Icons.Default.MedicalServices),
    BottomNavItem(Routes.PLAYER_CONSENT, "Consent", Icons.Default.Security)
)

val doctorNavItems = listOf(
    BottomNavItem(Routes.DOCTOR_HOME, "Home", Icons.Default.Home),
    BottomNavItem(Routes.DOCTOR_PLAYERS, "Players", Icons.Default.People),
    BottomNavItem(Routes.DOCTOR_TRAINING, "Training", Icons.Default.FitnessCenter),
    BottomNavItem(Routes.DOCTOR_PLAYBOOK, "Playbook", Icons.Default.MenuBook),
    BottomNavItem(Routes.DOCTOR_SETTINGS, "Settings", Icons.Default.Settings)
)

@Composable
fun GolazoNavHost() {
    val navController = rememberNavController()
    GolazoNavHostContent(navController)
}

@Composable
private fun GolazoNavHostContent(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val sessionManager = authViewModel.sessionManager
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if we should show bottom nav — visible on all authenticated screens
    val authRoutes = setOf(Routes.LOGIN, Routes.PIN_VERIFY, Routes.ONBOARDING, Routes.TERMS)
    val isAuthScreen = currentRoute in authRoutes
    val isPlayerScreen = currentRoute?.startsWith("player/") == true
    val isDoctorScreen = currentRoute?.startsWith("doctor/") == true
    val isSharedScreen = currentRoute in setOf(Routes.INTELLIGENCE, Routes.SIMULATIONS)
    val showBottomNav = !isAuthScreen && currentRoute != null && (isPlayerScreen || isDoctorScreen || isSharedScreen)

    // Pick the right nav items based on the current screen's role context
    val navItems = when {
        isDoctorScreen -> doctorNavItems
        isPlayerScreen -> playerNavItems
        isSharedScreen -> {
            val role = sessionManager.currentUser.value?.role
            if (role == "doctor") doctorNavItems else playerNavItems
        }
        else -> playerNavItems
    }

    // Determine which tab is "active" by matching the route prefix
    fun isTabSelected(tabRoute: String): Boolean {
        if (currentRoute == tabRoute) return true
        return currentRoute?.startsWith(tabRoute) == true && tabRoute != currentRoute
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = CardWhite,
                    tonalElevation = 12.dp
                ) {
                    val homeRoute = navItems.first().route
                    navItems.forEach { item ->
                        val selected = isTabSelected(item.route)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (item.route == homeRoute) {
                                    // Home tab: pop everything back to home
                                    navController.popBackStack(homeRoute, inclusive = false)
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(homeRoute) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UefaBlue,
                                selectedTextColor = UefaBlue,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = UefaBlueVeryLight
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(
                bottom = if (showBottomNav) paddingValues.calculateBottomPadding() else 0.dp
            )
        ) {
            // ===== AUTH =====
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { userId, isNewUser, requires2FA ->
                        when {
                            isNewUser -> navController.navigate(Routes.onboarding(userId)) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                            requires2FA -> navController.navigate(Routes.pinVerify(userId)) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                            else -> {
                                val user = sessionManager.currentUser.value
                                if (user?.tcAcceptedAt != null) {
                                    val homeRoute = if (user.role == "doctor") Routes.DOCTOR_HOME else Routes.PLAYER_HOME
                                    navController.navigate(homeRoute) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Routes.terms(userId)) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                )
            }

            composable(
                Routes.PIN_VERIFY,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                PinScreen(
                    userId = userId,
                    onVerified = {
                        val user = sessionManager.currentUser.value
                        if (user?.tcAcceptedAt != null) {
                            val homeRoute = if (user.role == "doctor") Routes.DOCTOR_HOME else Routes.PLAYER_HOME
                            navController.navigate(homeRoute) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.terms(userId)) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(
                Routes.ONBOARDING,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                OnboardingScreen(
                    userId = userId,
                    onComplete = {
                        navController.navigate(Routes.terms(userId)) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                Routes.TERMS,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val currentUser by sessionManager.currentUser.collectAsStateWithLifecycle()

                LaunchedEffect(currentUser) {
                    currentUser?.let { u ->
                        if (u.tcAcceptedAt != null) {
                            val homeRoute = if (u.role == "doctor") Routes.DOCTOR_HOME else Routes.PLAYER_HOME
                            navController.navigate(homeRoute) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                TermsScreen(
                    userId = userId,
                    onAccepted = {
                        val role = currentUser?.role ?: "player"
                        val homeRoute = if (role == "doctor") Routes.DOCTOR_HOME else Routes.PLAYER_HOME
                        navController.navigate(homeRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ===== PLAYER SCREENS =====
            composable(Routes.PLAYER_HOME) {
                PlayerHomeScreen(
                    onSignOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.PLAYER_INJURIES) {
                InjuriesListScreen(
                    onCreateInjury = { navController.navigate(Routes.PLAYER_INJURY_CREATE) },
                    onInjuryClick = { navController.navigate(Routes.playerInjuryDetail(it)) },
                    onHistoryClick = { navController.navigate(Routes.PLAYER_INJURY_HISTORY) }
                )
            }

            composable(Routes.PLAYER_INJURY_CREATE) {
                InjuryCreateScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                Routes.PLAYER_INJURY_DETAIL,
                arguments = listOf(navArgument("injuryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val injuryId = backStackEntry.arguments?.getString("injuryId") ?: ""
                InjuryDetailScreen(
                    injuryId = injuryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_INJURY_HISTORY) {
                InjuryHistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_WELLBEING) {
                WellbeingScreen(
                    onFindHelp = { navController.navigate(Routes.PLAYER_FIND_HELP) },
                    onSessions = { navController.navigate(Routes.PLAYER_SESSIONS) },
                    onBreathing = { navController.navigate(Routes.PLAYER_BREATHING) }
                )
            }

            composable(Routes.PLAYER_FIND_HELP) {
                FindHelpScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_SESSIONS) {
                SessionsScreen(
                    onBack = { navController.popBackStack() },
                    onSessionClick = { navController.navigate(Routes.playerSessionDetail(it)) }
                )
            }

            composable(
                Routes.PLAYER_SESSION_DETAIL,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                SessionDetailScreen(
                    sessionId = sessionId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_BREATHING) {
                BreathingScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_PCME) {
                PcmeListScreen(
                    onEntryClick = { navController.navigate(Routes.playerPcmeDetail(it)) }
                )
            }

            composable(
                Routes.PLAYER_PCME_DETAIL,
                arguments = listOf(navArgument("entryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                PcmeDetailScreen(
                    entryId = entryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PLAYER_CONSENT) {
                ConsentListScreen(
                    onCreateConsent = { navController.navigate(Routes.PLAYER_CONSENT_CREATE) }
                )
            }

            composable(Routes.PLAYER_CONSENT_CREATE) {
                ConsentCreateScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { token ->
                        if (token != null) {
                            navController.navigate(Routes.playerConsentInvite(token)) {
                                popUpTo(Routes.PLAYER_CONSENT)
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(
                Routes.PLAYER_CONSENT_INVITE,
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                ConsentInviteScreen(
                    token = token,
                    onBack = {
                        navController.popBackStack(Routes.PLAYER_CONSENT, inclusive = false)
                    }
                )
            }

            composable(Routes.PLAYER_PROFILE) {
                PlayerProfileScreen()
            }

            // ===== DOCTOR SCREENS =====
            composable(Routes.DOCTOR_HOME) {
                DoctorHomeScreen(
                    onViewPlayers = { navController.navigate(Routes.DOCTOR_PLAYERS) },
                    onViewInjuries = { navController.navigate(Routes.DOCTOR_INJURIES) },
                    onViewPcme = { navController.navigate(Routes.DOCTOR_PCME) },
                    onViewTraining = { navController.navigate(Routes.DOCTOR_TRAINING) }
                )
            }

            composable(Routes.DOCTOR_PLAYERS) {
                DoctorPlayersScreen(
                    onPlayerClick = { navController.navigate(Routes.doctorPlayerDetail(it)) }
                )
            }

            composable(
                Routes.DOCTOR_PLAYER_DETAIL,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                DoctorPlayerDetailScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DOCTOR_INJURIES) {
                DoctorInjuriesScreen(
                    onInjuryClick = { navController.navigate(Routes.doctorInjuryDetail(it)) },
                    onCreateInjury = { navController.navigate(Routes.DOCTOR_INJURY_CREATE) }
                )
            }

            composable(
                Routes.DOCTOR_INJURY_DETAIL,
                arguments = listOf(navArgument("injuryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val injuryId = backStackEntry.arguments?.getString("injuryId") ?: ""
                DoctorInjuryDetailScreen(
                    injuryId = injuryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DOCTOR_INJURY_CREATE) {
                DoctorInjuryCreateScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() }
                )
            }

            composable(Routes.DOCTOR_PCME) {
                DoctorPcmeListScreen(
                    onEntryClick = { navController.navigate(Routes.doctorPcmeDetail(it)) },
                    onCreatePcme = { navController.navigate(Routes.doctorPcmeForm(it)) }
                )
            }

            composable(
                Routes.DOCTOR_PCME_FORM,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                DoctorPcmeFormScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() }
                )
            }

            composable(
                Routes.DOCTOR_PCME_DETAIL,
                arguments = listOf(navArgument("entryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
                DoctorPcmeDetailScreen(
                    entryId = entryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Routes.DOCTOR_PCME_HISTORY,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                DoctorPcmeHistoryScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onEntryClick = { navController.navigate(Routes.doctorPcmeDetail(it)) }
                )
            }

            composable(Routes.DOCTOR_TRAINING) {
                TrainingScreen()
            }

            composable(Routes.DOCTOR_PLAYBOOK) {
                PlaybookScreen()
            }

            composable(Routes.DOCTOR_SETTINGS) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ===== SHARED SCREENS =====
            composable(Routes.INTELLIGENCE) {
                IntelligenceScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SIMULATIONS) {
                SimulationsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

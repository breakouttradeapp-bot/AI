package com.aikundli.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aikundli.ui.screens.*

sealed class Screen(val route: String) {
    object Splash         : Screen("splash")
    object Home           : Screen("home")
    object GenerateKundli : Screen("generate_kundli")
    object KundliResult   : Screen("kundli_result")
    object MatchKundli    : Screen("match_kundli")
    object MatchResult    : Screen("match_result")
    object DailyHoroscope : Screen("daily_horoscope")
    object SavedReports   : Screen("saved_reports")
    object Settings       : Screen("settings")
    object PrivacyPolicy  : Screen("privacy_policy")
    object Terms          : Screen("terms")
    object Premium        : Screen("premium")
}

@Composable
fun KundliNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.GenerateKundli.route) {
            GenerateKundliScreen(navController = navController)
        }
        composable(Screen.KundliResult.route) {
            KundliResultScreen(navController = navController)
        }
        composable(Screen.MatchKundli.route) {
            MatchKundliScreen(navController = navController)
        }
        composable(Screen.MatchResult.route) {
            MatchResultScreen(navController = navController)
        }
        composable(Screen.DailyHoroscope.route) {
            DailyHoroscopeScreen(navController = navController)
        }
        composable(Screen.SavedReports.route) {
            SavedReportsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(Screen.Terms.route) {
            TermsScreen(navController = navController)
        }
        composable(Screen.Premium.route) {
            PremiumScreen(navController = navController)
        }
    }
}

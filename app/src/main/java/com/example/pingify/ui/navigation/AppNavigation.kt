package com.example.pingify.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pingify.ui.screens.*
import com.example.pingify.ui.viewmodel.AuthState
import com.example.pingify.ui.viewmodel.AuthViewModel

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    const val CHAT = "chat/{friendId}/{friendName}"

    fun chatRoute(friendId: String, friendName: String) =
        "chat/${Uri.encode(friendId)}/${Uri.encode(friendName)}"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.state.collectAsState()

    val startDest = when (authState) {
        is AuthState.Loading -> Routes.LOGIN
        is AuthState.Unauthenticated -> Routes.LOGIN
        is AuthState.NeedsOnboarding -> Routes.ONBOARDING
        is AuthState.Authenticated -> Routes.MAIN
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignup = { navController.navigate(Routes.SIGNUP) },
                onLoginSuccess = { user ->
                    val dest = if (user.isOnboarded) Routes.MAIN else Routes.ONBOARDING
                    navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignupScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onSignupSuccess = {
                    navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SIGNUP) { inclusive = true } }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                authViewModel = authViewModel,
                onComplete = {
                    navController.navigate(Routes.MAIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                authViewModel = authViewModel,
                onNavigateToChat = { id, name ->
                    navController.navigate(Routes.chatRoute(id, name))
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(
                navArgument("friendId") { type = NavType.StringType },
                navArgument("friendName") { type = NavType.StringType }
            )
        ) { back ->
            val friendId = Uri.decode(back.arguments?.getString("friendId") ?: "")
            val friendName = Uri.decode(back.arguments?.getString("friendName") ?: "")
            ChatScreen(
                authViewModel = authViewModel,
                friendId = friendId,
                friendName = friendName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

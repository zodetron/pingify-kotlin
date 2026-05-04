package com.example.pingify.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pingify.ui.viewmodel.AuthViewModel
import com.example.pingify.ui.viewmodel.FriendsViewModel
import com.example.pingify.ui.viewmodel.HomeViewModel
import com.example.pingify.ui.viewmodel.NotificationsViewModel

private const val TAB_HOME = "tab_home"
private const val TAB_FRIENDS = "tab_friends"
private const val TAB_NOTIFICATIONS = "tab_notifications"

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onNavigateToChat: (friendId: String, friendName: String) -> Unit,
    onLogout: () -> Unit
) {
    val tabNav = rememberNavController()
    val currentTab by tabNav.currentBackStackEntryAsState()

    val homeVm = remember { HomeViewModel() }
    val friendsVm = remember { FriendsViewModel() }
    val notifVm = remember { NotificationsViewModel() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple(TAB_HOME, "Home", Icons.Default.Home),
                    Triple(TAB_FRIENDS, "Friends", Icons.Default.People),
                    Triple(TAB_NOTIFICATIONS, "Notifications", Icons.Default.Notifications)
                ).forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentTab?.destination?.route == route,
                        onClick = {
                            tabNav.navigate(route) {
                                popUpTo(TAB_HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = tabNav, startDestination = TAB_HOME, modifier = Modifier.padding(padding)) {
            composable(TAB_HOME) {
                HomeScreen(
                    authViewModel = authViewModel,
                    viewModel = homeVm,
                    onNavigateToChat = onNavigateToChat,
                    onLogout = onLogout
                )
            }
            composable(TAB_FRIENDS) {
                FriendsScreen(viewModel = friendsVm, onNavigateToChat = onNavigateToChat)
            }
            composable(TAB_NOTIFICATIONS) {
                NotificationsScreen(viewModel = notifVm)
            }
        }
    }
}

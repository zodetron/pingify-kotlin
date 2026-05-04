package com.example.pingify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pingify.data.model.User
import com.example.pingify.ui.viewmodel.AuthState
import com.example.pingify.ui.viewmodel.AuthViewModel
import com.example.pingify.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel,
    onNavigateToChat: (friendId: String, friendName: String) -> Unit,
    onLogout: () -> Unit
) {
    val friends by viewModel.friends.collectAsState()
    val recommended by viewModel.recommended.collectAsState()
    val outgoing by viewModel.outgoing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toast by viewModel.toast.collectAsState()
    val authState by authViewModel.state.collectAsState()
    val currentUser = (authState as? AuthState.Authenticated)?.user

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) onLogout()
    }

    toast?.let { msg ->
        LaunchedEffect(msg) {
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pingify", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        currentUser?.let {
                            Text("Hi, ${it.fullName.split(" ").first()} 👋", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load() }) { Icon(Icons.Default.Refresh, "Refresh") }
                    IconButton(onClick = { authViewModel.logout() }) { Icon(Icons.Default.Logout, "Logout") }
                }
            )
        }
    ) { padding ->
        if (isLoading && friends.isEmpty() && recommended.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (friends.isNotEmpty()) {
                item {
                    Text(
                        "Your Friends (${friends.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(friends) { friend ->
                    FriendCard(
                        user = friend,
                        actionLabel = "Message",
                        actionIcon = { Icon(Icons.Default.Chat, null) },
                        onAction = { onNavigateToChat(friend.id, friend.fullName) }
                    )
                }
            }

            if (recommended.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Developers You May Know",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(recommended) { user ->
                    val alreadySent = outgoing.any { it.recipient?.id == user.id }
                    FriendCard(
                        user = user,
                        actionLabel = if (alreadySent) "Sent" else "Connect",
                        actionIcon = { Icon(Icons.Default.PersonAdd, null) },
                        onAction = {
                            if (!alreadySent) viewModel.sendFriendRequest(user.id)
                        },
                        actionEnabled = !alreadySent
                    )
                }
            }

            if (friends.isEmpty() && recommended.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", style = MaterialTheme.typography.displayMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("No developers found yet", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Pull to refresh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendCard(
    user: User,
    actionLabel: String,
    actionIcon: @Composable () -> Unit,
    onAction: () -> Unit,
    actionEnabled: Boolean = true
) {
    val tech = listOfNotNull(
        user.nativeLanguage.takeIf { it.isNotBlank() },
        user.learningLanguage.takeIf { it.isNotBlank() }
    ).joinToString(" • ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.profilePic.ifBlank { null },
                contentDescription = user.fullName,
                modifier = Modifier.size(52.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, fontWeight = FontWeight.SemiBold)
                if (tech.isNotBlank()) {
                    Text(tech, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                if (user.location.isNotBlank()) {
                    Text(
                        "📍 ${user.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FilledTonalButton(
                onClick = onAction,
                enabled = actionEnabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                actionIcon()
                Spacer(Modifier.width(4.dp))
                Text(actionLabel)
            }
        }
    }
}

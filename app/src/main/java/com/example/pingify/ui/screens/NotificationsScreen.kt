package com.example.pingify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pingify.data.model.FriendRequest
import com.example.pingify.ui.viewmodel.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel) {
    val incoming by viewModel.incoming.collectAsState()
    val accepted by viewModel.accepted.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toast by viewModel.toast.collectAsState()

    toast?.let { msg ->
        LaunchedEffect(msg) { viewModel.clearToast() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Notifications", fontWeight = FontWeight.Bold) })

        if (isLoading && incoming.isEmpty() && accepted.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (incoming.isEmpty() && accepted.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("No notifications yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Friend requests will appear here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (incoming.isNotEmpty()) {
                item {
                    Text(
                        "Incoming Requests (${incoming.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(incoming) { request ->
                    IncomingRequestCard(request = request, onAccept = { viewModel.acceptRequest(request.id) })
                }
            }

            if (accepted.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "New Friends 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(accepted) { request ->
                    AcceptedRequestCard(request = request)
                }
            }
        }
    }
}

@Composable
private fun IncomingRequestCard(request: FriendRequest, onAccept: () -> Unit) {
    val sender = request.sender ?: return

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
                model = sender.profilePic.ifBlank { null },
                contentDescription = sender.fullName,
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(sender.fullName, fontWeight = FontWeight.SemiBold)
                val tech = buildString {
                    if (sender.nativeLanguage.isNotBlank()) append("Knows: ${sender.nativeLanguage}")
                    if (sender.learningLanguage.isNotBlank()) {
                        if (isNotEmpty()) append("  •  ")
                        append("Learning: ${sender.learningLanguage}")
                    }
                }
                if (tech.isNotBlank()) {
                    Text(tech, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            FilledTonalButton(
                onClick = onAccept,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Accept")
            }
        }
    }
}

@Composable
private fun AcceptedRequestCard(request: FriendRequest) {
    val recipient = request.recipient ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = recipient.profilePic.ifBlank { null },
                contentDescription = recipient.fullName,
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(recipient.fullName, fontWeight = FontWeight.SemiBold)
                Text(
                    "You are now friends! 🎉",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

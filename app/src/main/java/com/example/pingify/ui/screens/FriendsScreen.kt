package com.example.pingify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pingify.data.model.ALL_TECHNOLOGIES
import com.example.pingify.ui.viewmodel.FriendsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel,
    onNavigateToChat: (friendId: String, friendName: String) -> Unit
) {
    val search by viewModel.search.collectAsState()
    val techFilter by viewModel.techFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val friends by viewModel.filtered.collectAsState(initial = emptyList())

    // Most common tech options for filter chips
    val filterTechs = remember {
        listOf("javascript", "python", "kotlin", "react", "nodejs", "flutter", "typescript", "java")
            .mapNotNull { id -> ALL_TECHNOLOGIES.find { it.id == id } }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Friends", fontWeight = FontWeight.Bold) })

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.setSearch(it) },
                placeholder = { Text("Search by name or location...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterTechs) { tech ->
                    FilterChip(
                        selected = techFilter == tech.id,
                        onClick = { viewModel.setTechFilter(tech.id) },
                        label = { Text("${tech.emoji} ${tech.label}") }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (isLoading && friends.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (friends.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👥", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("No friends found", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Connect with developers on the Home tab",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(friends) { friend ->
                FriendCard(
                    user = friend,
                    actionLabel = "Message",
                    actionIcon = { Icon(Icons.Default.Chat, null) },
                    onAction = { onNavigateToChat(friend.id, friend.fullName) }
                )
            }
        }
    }
}

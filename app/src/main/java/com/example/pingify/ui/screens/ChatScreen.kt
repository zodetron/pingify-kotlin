package com.example.pingify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.pingify.ui.viewmodel.AuthState
import com.example.pingify.ui.viewmodel.AuthViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    authViewModel: AuthViewModel,
    friendId: String,
    friendName: String,
    onBack: () -> Unit
) {
    val authState by authViewModel.state.collectAsState()
    val currentUser = (authState as? AuthState.Authenticated)?.user
    val streamReady by authViewModel.streamReady.collectAsState()
    val scope = rememberCoroutineScope()

    var channelId by remember { mutableStateOf<String?>(null) }
    var isCreatingChannel by remember { mutableStateOf(true) }
    var channelError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser?.id, friendId, streamReady) {
        if (currentUser == null || !streamReady) return@LaunchedEffect
        isCreatingChannel = true
        channelError = null
        scope.launch {
            try {
                val result = ChatClient.instance().createChannel(
                    channelType = "messaging",
                    channelId = "",
                    memberIds = listOf(currentUser.id, friendId),
                    extraData = emptyMap()
                ).await()
                if (result.isSuccess) {
                    channelId = result.getOrThrow().cid
                } else {
                    channelError = result.errorOrNull()?.message
                        ?: "Could not open chat."
                }
            } catch (e: Exception) {
                channelError = e.message ?: "Could not open chat."
            }
            isCreatingChannel = false
        }
    }

    when {
        isCreatingChannel -> {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(friendName, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                    }
                )
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        channelError != null -> {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(friendName) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                    }
                )
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️", style = MaterialTheme.typography.displayMedium)
                        Text(channelError!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        channelId != null -> {
            ChatTheme {
                MessagesScreen(
                    viewModelFactory = MessagesViewModelFactory(
                        context = androidx.compose.ui.platform.LocalContext.current,
                        channelId = channelId!!,
                        messageLimit = 30
                    ),
                    onBackPressed = onBack
                )
            }
        }
    }
}

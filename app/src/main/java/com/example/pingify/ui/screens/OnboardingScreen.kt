package com.example.pingify.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.pingify.data.model.ALL_TECHNOLOGIES
import com.example.pingify.ui.viewmodel.AuthState
import com.example.pingify.ui.viewmodel.AuthViewModel

@Composable
fun OnboardingScreen(
    authViewModel: AuthViewModel,
    onComplete: () -> Unit
) {
    val state by authViewModel.state.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    val existingUser = (state as? AuthState.NeedsOnboarding)?.user
        ?: (state as? AuthState.Authenticated)?.user

    var fullName by remember { mutableStateOf(existingUser?.fullName ?: "") }
    var bio by remember { mutableStateOf(existingUser?.bio ?: "") }
    var nativeLanguage by remember { mutableStateOf(existingUser?.nativeLanguage ?: "") }
    var learningLanguage by remember { mutableStateOf(existingUser?.learningLanguage ?: "") }
    var location by remember { mutableStateOf(existingUser?.location ?: "") }
    var profilePic by remember { mutableStateOf(existingUser?.profilePic ?: "") }

    var showNativeSelector by remember { mutableStateOf(false) }
    var showLearningSelector by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) onComplete()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Complete Your Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Tell other developers about yourself",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Avatar
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    if (profilePic.isNotBlank()) {
                        AsyncImage(
                            model = profilePic,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center).size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val idx = (1..50).random()
                    OutlinedButton(onClick = {
                        profilePic = "https://xsgames.co/randomusers/assets/avatars/male/${(1..50).random()}.jpg"
                    }, shape = RoundedCornerShape(8.dp)) { Text("👨 Male") }
                    OutlinedButton(onClick = {
                        profilePic = "https://xsgames.co/randomusers/assets/avatars/female/${(1..50).random()}.jpg"
                    }, shape = RoundedCornerShape(8.dp)) { Text("👩 Female") }
                    OutlinedButton(onClick = {
                        profilePic = "https://xsgames.co/randomusers/assets/avatars/pixel/${(1..50).random()}.jpg"
                    }, shape = RoundedCornerShape(8.dp)) { Text("🎨 Pixel") }
                }
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                placeholder = { Text("Tell others about your coding journey...") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                maxLines = 4
            )

            // Tech selectors
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🏆 Strongest Language", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().clickable { showNativeSelector = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (nativeLanguage.isBlank()) "Select language" else
                                ALL_TECHNOLOGIES.find { it.id == nativeLanguage }
                                    ?.let { "${it.emoji} ${it.label}" } ?: nativeLanguage,
                            modifier = Modifier.padding(12.dp),
                            color = if (nativeLanguage.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("📚 Currently Learning", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().clickable { showLearningSelector = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (learningLanguage.isBlank()) "Select language" else
                                ALL_TECHNOLOGIES.find { it.id == learningLanguage }
                                    ?.let { "${it.emoji} ${it.label}" } ?: learningLanguage,
                            modifier = Modifier.padding(12.dp),
                            color = if (learningLanguage.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("City, Country") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    authViewModel.onboard(fullName, bio, nativeLanguage, learningLanguage, location)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && fullName.isNotBlank() && bio.isNotBlank() &&
                    nativeLanguage.isNotBlank() && learningLanguage.isNotBlank() && location.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Complete Profile", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showNativeSelector) {
        TechSelectorDialog(
            title = "Your Strongest Language",
            selected = nativeLanguage,
            onSelect = { nativeLanguage = it; showNativeSelector = false },
            onDismiss = { showNativeSelector = false }
        )
    }

    if (showLearningSelector) {
        TechSelectorDialog(
            title = "Currently Learning",
            selected = learningLanguage,
            onSelect = { learningLanguage = it; showLearningSelector = false },
            onDismiss = { showLearningSelector = false }
        )
    }
}

@Composable
fun TechSelectorDialog(
    title: String,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = ALL_TECHNOLOGIES.filter {
        search.isBlank() || it.label.contains(search, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(filtered) { tech ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(tech.id) }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(tech.emoji, style = MaterialTheme.typography.titleMedium)
                            Text(tech.label, modifier = Modifier.weight(1f))
                            if (tech.id == selected) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

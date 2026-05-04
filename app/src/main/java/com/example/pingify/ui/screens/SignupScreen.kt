package com.example.pingify.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pingify.ui.viewmodel.AuthState
import com.example.pingify.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val state by authViewModel.state.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.NeedsOnboarding) onSignupSuccess()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔗", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Join Pingify",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Connect with developers worldwide",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Create your account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (error != null) {
                        Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = {
                            authViewModel.clearError()
                            authViewModel.signup(fullName, email, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = !isLoading && fullName.isNotBlank() && email.isNotBlank() && password.length >= 6,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Create Account", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row {
                Text("Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Sign in",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

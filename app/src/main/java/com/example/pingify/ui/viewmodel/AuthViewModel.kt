package com.example.pingify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pingify.data.model.OnboardingRequest
import com.example.pingify.data.model.User
import com.example.pingify.data.network.STREAM_API_KEY
import com.example.pingify.data.repository.AuthRepository
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User as StreamUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class NeedsOnboarding(val user: User) : AuthState()
}

class AuthViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _streamReady = MutableStateFlow(false)
    val streamReady: StateFlow<Boolean> = _streamReady

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repo.getMe()
                .onSuccess { user ->
                    _state.value = if (user.isOnboarded) AuthState.Authenticated(user)
                    else AuthState.NeedsOnboarding(user)
                    connectStreamUser(user)
                }
                .onFailure { _state.value = AuthState.Unauthenticated }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.login(email, password)
                .onSuccess { res ->
                    connectStreamUser(res.user)
                    _state.value = if (res.user.isOnboarded) AuthState.Authenticated(res.user)
                    else AuthState.NeedsOnboarding(res.user)
                }
                .onFailure { _error.value = it.message ?: "Login failed" }
            _isLoading.value = false
        }
    }

    fun signup(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.signup(fullName, email, password)
                .onSuccess { res ->
                    connectStreamUser(res.user)
                    _state.value = AuthState.NeedsOnboarding(res.user)
                }
                .onFailure { _error.value = it.message ?: "Signup failed" }
            _isLoading.value = false
        }
    }

    fun onboard(
        fullName: String,
        bio: String,
        nativeLanguage: String,
        learningLanguage: String,
        location: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.onboard(OnboardingRequest(fullName, bio, nativeLanguage, learningLanguage, location))
                .onSuccess { res -> _state.value = AuthState.Authenticated(res.user) }
                .onFailure { _error.value = it.message ?: "Onboarding failed" }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _streamReady.value = false
            ChatClient.instance().disconnect(flushPersistence = false).await()
            repo.logout()
            _state.value = AuthState.Unauthenticated
        }
    }

    fun clearError() { _error.value = null }

    private fun connectStreamUser(user: User) {
        if (ChatClient.instance().getCurrentUser() != null) {
            _streamReady.value = true
            return
        }
        _streamReady.value = false
        viewModelScope.launch {
            repo.getChatToken()
                .onSuccess { token ->
                    val streamUser = StreamUser(
                        id = user.id,
                        name = user.fullName,
                        image = user.profilePic
                    )
                    val result = ChatClient.instance().connectUser(streamUser, token).await()
                    if (result.isSuccess) _streamReady.value = true
                }
        }
    }
}

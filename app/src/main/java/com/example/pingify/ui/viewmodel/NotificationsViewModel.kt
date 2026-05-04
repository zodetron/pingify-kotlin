package com.example.pingify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pingify.data.model.FriendRequest
import com.example.pingify.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {

    private val _incoming = MutableStateFlow<List<FriendRequest>>(emptyList())
    val incoming: StateFlow<List<FriendRequest>> = _incoming

    private val _accepted = MutableStateFlow<List<FriendRequest>>(emptyList())
    val accepted: StateFlow<List<FriendRequest>> = _accepted

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getFriendRequests()
                .onSuccess { res ->
                    _incoming.value = res.incomingRequests
                    _accepted.value = res.acceptedRequests
                }
            _isLoading.value = false
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            repo.acceptFriendRequest(requestId)
                .onSuccess {
                    _toast.value = "Friend request accepted!"
                    load()
                }
                .onFailure { _toast.value = it.message ?: "Failed to accept" }
        }
    }

    fun clearToast() { _toast.value = null }
}

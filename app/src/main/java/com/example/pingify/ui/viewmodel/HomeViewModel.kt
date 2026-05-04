package com.example.pingify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pingify.data.model.FriendRequest
import com.example.pingify.data.model.User
import com.example.pingify.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {

    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends

    private val _recommended = MutableStateFlow<List<User>>(emptyList())
    val recommended: StateFlow<List<User>> = _recommended

    private val _outgoing = MutableStateFlow<List<FriendRequest>>(emptyList())
    val outgoing: StateFlow<List<FriendRequest>> = _outgoing

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            launch { repo.getMyFriends().onSuccess { _friends.value = it } }
            launch { repo.getRecommendedUsers().onSuccess { _recommended.value = it } }
            launch { repo.getOutgoingFriendRequests().onSuccess { _outgoing.value = it } }
            _isLoading.value = false
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            repo.sendFriendRequest(userId)
                .onSuccess {
                    _toast.value = "Friend request sent!"
                    load()
                }
                .onFailure { _toast.value = it.message ?: "Failed to send request" }
        }
    }

    fun clearToast() { _toast.value = null }
}

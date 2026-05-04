package com.example.pingify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pingify.data.model.User
import com.example.pingify.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FriendsViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {

    private val _allFriends = MutableStateFlow<List<User>>(emptyList())
    private val _search = MutableStateFlow("")
    private val _techFilter = MutableStateFlow("")

    val search: StateFlow<String> = _search
    val techFilter: StateFlow<String> = _techFilter

    val filtered = combine(_allFriends, _search, _techFilter) { friends, query, tech ->
        friends.filter { user ->
            val matchesSearch = query.isBlank() ||
                user.fullName.contains(query, ignoreCase = true) ||
                user.location.contains(query, ignoreCase = true)
            val matchesTech = tech.isBlank() ||
                user.nativeLanguage.equals(tech, ignoreCase = true) ||
                user.learningLanguage.equals(tech, ignoreCase = true)
            matchesSearch && matchesTech
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getMyFriends().onSuccess { _allFriends.value = it }
            _isLoading.value = false
        }
    }

    fun setSearch(query: String) { _search.value = query }
    fun setTechFilter(tech: String) { _techFilter.value = if (_techFilter.value == tech) "" else tech }
}

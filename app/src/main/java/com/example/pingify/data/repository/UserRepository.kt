package com.example.pingify.data.repository

import com.example.pingify.data.model.*
import com.example.pingify.data.network.NetworkClient

class UserRepository {
    private val api = NetworkClient.apiService

    suspend fun getRecommendedUsers(): Result<List<User>> =
        runCatching {
            val res = api.getRecommendedUsers()
            if (res.isSuccessful) res.body()?.users ?: emptyList()
            else emptyList()
        }

    suspend fun getMyFriends(): Result<List<User>> =
        runCatching {
            val res = api.getMyFriends()
            if (res.isSuccessful) res.body() ?: emptyList()
            else emptyList()
        }

    suspend fun sendFriendRequest(userId: String): Result<Unit> =
        runCatching {
            val res = api.sendFriendRequest(userId)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: "Failed to send request")
        }

    suspend fun acceptFriendRequest(requestId: String): Result<Unit> =
        runCatching {
            val res = api.acceptFriendRequest(requestId)
            if (!res.isSuccessful) error(res.errorBody()?.string() ?: "Failed to accept request")
        }

    suspend fun getFriendRequests(): Result<FriendRequestsResponse> =
        runCatching {
            val res = api.getFriendRequests()
            if (res.isSuccessful) res.body()!!
            else error("Failed to fetch requests")
        }

    suspend fun getOutgoingFriendRequests(): Result<List<FriendRequest>> =
        runCatching {
            val res = api.getOutgoingFriendRequests()
            if (res.isSuccessful) res.body() ?: emptyList()
            else emptyList()
        }
}

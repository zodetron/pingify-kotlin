package com.example.pingify.data.network

import com.example.pingify.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/onboarding")
    suspend fun onboard(@Body request: OnboardingRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<User>

    @GET("users")
    suspend fun getRecommendedUsers(): Response<UsersResponse>

    @GET("users/friends")
    suspend fun getMyFriends(): Response<List<User>>

    @POST("users/friend-request/{id}")
    suspend fun sendFriendRequest(@Path("id") userId: String): Response<FriendRequest>

    @PUT("users/friend-request/{id}/accept")
    suspend fun acceptFriendRequest(@Path("id") requestId: String): Response<FriendRequest>

    @GET("users/friend-requests")
    suspend fun getFriendRequests(): Response<FriendRequestsResponse>

    @GET("users/outgoing-friend-requests")
    suspend fun getOutgoingFriendRequests(): Response<List<FriendRequest>>

    @GET("chat/token")
    suspend fun getChatToken(): Response<StreamTokenResponse>
}

package com.example.pingify.data.repository

import com.example.pingify.data.model.*
import com.example.pingify.data.network.NetworkClient

class AuthRepository {
    private val api = NetworkClient.apiService

    suspend fun signup(fullName: String, email: String, password: String): Result<AuthResponse> =
        runCatching {
            val res = api.signup(SignupRequest(email, password, fullName))
            if (res.isSuccessful) res.body()!!
            else error(res.errorBody()?.string() ?: "Signup failed")
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        runCatching {
            val res = api.login(LoginRequest(email, password))
            if (res.isSuccessful) res.body()!!
            else error(res.errorBody()?.string() ?: "Login failed")
        }

    suspend fun logout(): Result<Unit> =
        runCatching {
            api.logout()
            NetworkClient.cookieJar.clearAll()
        }

    suspend fun onboard(request: OnboardingRequest): Result<AuthResponse> =
        runCatching {
            val res = api.onboard(request)
            if (res.isSuccessful) res.body()!!
            else error(res.errorBody()?.string() ?: "Onboarding failed")
        }

    suspend fun getMe(): Result<User> =
        runCatching {
            val res = api.getMe()
            if (res.isSuccessful) res.body()!!
            else error("Not authenticated")
        }

    suspend fun getChatToken(): Result<String> =
        runCatching {
            val res = api.getChatToken()
            if (res.isSuccessful) res.body()!!.token
            else error("Could not get chat token")
        }
}

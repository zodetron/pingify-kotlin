package com.example.pingify.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id") val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val bio: String = "",
    val profilePic: String = "",
    val nativeLanguage: String = "",
    val learningLanguage: String = "",
    val location: String = "",
    val isOnboarded: Boolean = false,
    val friends: List<String> = emptyList()
)

data class FriendRequest(
    @SerializedName("_id") val id: String = "",
    val sender: User? = null,
    val recipient: User? = null,
    val status: String = "pending"
)

data class LoginRequest(val email: String, val password: String)
data class SignupRequest(val email: String, val password: String, val fullName: String)
data class OnboardingRequest(
    val fullName: String,
    val bio: String,
    val nativeLanguage: String,
    val learningLanguage: String,
    val location: String
)

data class AuthResponse(val success: Boolean, val user: User)
data class UsersResponse(val users: List<User>)
data class FriendRequestsResponse(
    val incomingRequests: List<FriendRequest>,
    val acceptedRequests: List<FriendRequest>
)
data class StreamTokenResponse(val token: String)

data class TechOption(val id: String, val label: String, val emoji: String)

val ALL_TECHNOLOGIES = listOf(
    TechOption("javascript", "JavaScript", "🟨"),
    TechOption("typescript", "TypeScript", "🔷"),
    TechOption("python", "Python", "🐍"),
    TechOption("java", "Java", "☕"),
    TechOption("kotlin", "Kotlin", "🟣"),
    TechOption("swift", "Swift", "🦅"),
    TechOption("dart", "Dart", "🎯"),
    TechOption("csharp", "C#", "💜"),
    TechOption("cplusplus", "C++", "⚡"),
    TechOption("go", "Go", "🐹"),
    TechOption("rust", "Rust", "🦀"),
    TechOption("php", "PHP", "🐘"),
    TechOption("ruby", "Ruby", "💎"),
    TechOption("r", "R", "📊"),
    TechOption("scala", "Scala", "📐"),
    TechOption("react", "React", "⚛️"),
    TechOption("nextjs", "Next.js", "▲"),
    TechOption("vue", "Vue.js", "🌿"),
    TechOption("angular", "Angular", "🔴"),
    TechOption("svelte", "Svelte", "🔶"),
    TechOption("nodejs", "Node.js", "🟢"),
    TechOption("express", "Express", "🚂"),
    TechOption("django", "Django", "🎸"),
    TechOption("flask", "Flask", "🧪"),
    TechOption("spring", "Spring", "🌱"),
    TechOption("fastapi", "FastAPI", "⚡"),
    TechOption("flutter", "Flutter", "🔵"),
    TechOption("reactnative", "React Native", "📱"),
    TechOption("mongodb", "MongoDB", "🍃"),
    TechOption("postgresql", "PostgreSQL", "🐘"),
    TechOption("mysql", "MySQL", "🐬"),
    TechOption("redis", "Redis", "🔴"),
    TechOption("firebase", "Firebase", "🔥"),
    TechOption("docker", "Docker", "🐳"),
    TechOption("kubernetes", "Kubernetes", "⚙️"),
    TechOption("aws", "AWS", "☁️"),
    TechOption("git", "Git", "📝"),
).sortedBy { it.label }

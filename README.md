# Pingify — Android

A native Android app for developers to find, connect, and chat with other developers based on shared tech stacks. Built with Kotlin + Jetpack Compose, backed by a Node.js/Express + MongoDB API.

---

## Features

### Working
- **Auth** — Sign up, log in, log out with JWT (httpOnly cookie, persists across app restarts)
- **Onboarding** — Pick an avatar, set your native language, learning language, bio, and location
- **Home** — See your friends and developer recommendations filtered by tech stack
- **Friend Requests** — Send, accept, and track incoming/outgoing requests
- **Notifications** — Real-time friend request updates
- **Chat** — 1-on-1 messaging powered by [Stream Chat](https://getstream.io) with full message history

### In Progress
- **Voice Calls** — UI placeholder exists, Stream SDK supports it — integration pending
- **Video Calls** — Same as above, needs Stream Video SDK wired up

---

## Tech Stack

| Layer | Tech |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Networking | Retrofit 2 + OkHttp 3 |
| Auth | JWT via httpOnly cookie (custom CookieJar) |
| Messaging | Stream Chat Android Compose SDK |
| Images | Coil Compose |
| State | ViewModel + StateFlow |
| Backend | Node.js, Express, MongoDB, Stream Chat Server SDK |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 26+
- A running instance of the [Pingify backend](../Pingify%20-%20Web%20Copy/backend) **or** a deployed backend URL
- A [Stream](https://getstream.io) account with an app and API key

### Setup

1. Clone the repo and open the `Pingify` folder in Android Studio.

2. Create `local.properties` in the project root (already gitignored):

```properties
sdk.dir=/path/to/your/Android/sdk

BASE_URL=https://your-backend-url.com/api/
STREAM_API_KEY=your_stream_api_key
```

3. Sync Gradle, build, and run on an emulator or device.

> For local development with an emulator, use `BASE_URL=http://10.0.2.2:5001/api/`

---

## Project Structure

```
app/src/main/java/com/example/pingify/
├── data/
│   ├── model/          # Data classes (User, FriendRequest, etc.)
│   ├── network/        # Retrofit ApiService + OkHttp CookieJar
│   └── repository/     # AuthRepository, UserRepository
├── ui/
│   ├── navigation/     # AppNavigation, Routes
│   ├── screens/        # Login, Signup, Onboarding, Home, Friends, Chat, ...
│   ├── viewmodel/      # AuthViewModel, HomeViewModel, FriendsViewModel, ...
│   └── theme/          # Material 3 color scheme (indigo/violet)
├── MainActivity.kt
└── PingifyApp.kt       # Application class — initializes NetworkClient + ChatClient
```

---

## Contributing / Known Issues

- Voice and video calls are the next feature to implement — the Stream Video SDK needs to be added and integrated with the existing chat channel setup
- Pull requests welcome

---

## License

MIT

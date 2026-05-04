package com.example.pingify.data.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BASE_URL: String get() = com.example.pingify.BuildConfig.BASE_URL
val STREAM_API_KEY: String get() = com.example.pingify.BuildConfig.STREAM_API_KEY

class AppCookieJar(context: Context) : CookieJar {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pingify_cookies", Context.MODE_PRIVATE)
    private val store = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val bucket = store.getOrPut(url.host) { mutableListOf() }
        cookies.forEach { incoming ->
            bucket.removeAll { it.name == incoming.name }
            if (incoming.value.isNotEmpty()) {
                bucket.add(incoming)
                if (incoming.name == "jwt") {
                    prefs.edit().putString("jwt", incoming.value).apply()
                }
            } else if (incoming.name == "jwt") {
                prefs.edit().remove("jwt").apply()
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val inMemory = store[url.host]
        if (!inMemory.isNullOrEmpty()) return inMemory

        val saved = prefs.getString("jwt", null) ?: return emptyList()
        val restored = Cookie.Builder()
            .domain(url.host)
            .path("/")
            .name("jwt")
            .value(saved)
            .expiresAt(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)
            .build()
        store[url.host] = mutableListOf(restored)
        return listOf(restored)
    }

    fun clearAll() {
        store.clear()
        prefs.edit().clear().apply()
    }
}

object NetworkClient {
    lateinit var apiService: ApiService
        private set
    lateinit var cookieJar: AppCookieJar
        private set

    fun init(context: Context) {
        cookieJar = AppCookieJar(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

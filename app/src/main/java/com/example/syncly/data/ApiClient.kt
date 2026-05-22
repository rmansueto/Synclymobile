package com.example.syncly.data

import okhttp3.OkHttpClient
import okhttp3.Request

object ApiClient {

    private const val BASE_URL = "https://syncly-fofr.onrender.com"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    fun getClient(): OkHttpClient = httpClient
    fun getBaseUrl(): String = BASE_URL

    /** Headers for public endpoints (login, register) */
    fun newRequestBuilder(): Request.Builder =
        Request.Builder()
            .addHeader("Content-Type", "application/json")

    /** Headers for authenticated endpoints — attaches your Spring Boot JWT */
    fun newAuthenticatedRequestBuilder(accessToken: String): Request.Builder =
        newRequestBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
}
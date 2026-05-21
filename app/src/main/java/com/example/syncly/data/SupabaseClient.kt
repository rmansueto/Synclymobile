package com.example.syncly.data

import okhttp3.OkHttpClient
import okhttp3.Request

object SupabaseClient {

    private var baseUrl: String = ""
    private var anonKey: String = ""

    // Renamed to `okHttpClient` so its auto-generated getter
    // `getOkHttpClient()` no longer clashes with any explicit function
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    fun init(url: String, anonKey: String) {
        this.baseUrl = url
        this.anonKey = anonKey
    }

    fun getBaseUrl(): String = baseUrl

    fun getClient(): OkHttpClient = okHttpClient

    /** Headers for public (unauthenticated) Supabase requests. */
    fun newRequestBuilder(): Request.Builder =
        Request.Builder()
            .addHeader("apikey", anonKey)
            .addHeader("Content-Type", "application/json")

    /** Headers for authenticated requests — attaches the user's JWT. */
    fun newAuthenticatedRequestBuilder(accessToken: String): Request.Builder =
        newRequestBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
}
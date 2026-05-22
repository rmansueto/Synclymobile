package com.example.syncly.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository {

    private val client = ApiClient
    private val gson   = Gson()
    private val JSON   = "application/json; charset=utf-8".toMediaType()

    // ── Result wrapper ────────────────────────────────────────────────────────

    data class AuthResult(
        val success: Boolean,
        val accessToken: String? = null,
        val user: User? = null,
        val errorMessage: String? = null
    )

    // ── Login ─────────────────────────────────────────────────────────────────
    // POST /api/auth/login
    // Body: { "email": "...", "password": "..." }
    // Returns: { "token": "...", "email": "..." }
    // Then fetches full profile from GET /api/users/me

    fun login(email: String, password: String): AuthResult {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }

            val request = client.newRequestBuilder()
                .url("${client.getBaseUrl()}/api/auth/login")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json  = gson.fromJson(raw, JsonObject::class.java)
                    val token = json.get("token").asString

                    // Fetch full user profile using the token
                    val user = getMe(token)
                    AuthResult(success = true, accessToken = token, user = user)
                } else {
                    val err = gson.fromJson(raw, JsonObject::class.java)
                    val msg = err.get("message")?.asString
                        ?: err.get("error")?.asString
                        ?: "Login failed. Check your email and password."
                    AuthResult(success = false, errorMessage = msg)
                }
            }
        } catch (e: Exception) {
            AuthResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }


    fun register(email: String, password: String, fullName: String): AuthResult {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
                addProperty("fullName", fullName)
            }

            val request = client.newRequestBuilder()
                .url("${client.getBaseUrl()}/api/auth/register")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json  = gson.fromJson(raw, JsonObject::class.java)
                    val token = json.get("token")?.asString

                    // Fetch full profile after register
                    val user = if (token != null) getMe(token) else null
                    AuthResult(success = true, accessToken = token, user = user)
                } else {
                    val err = gson.fromJson(raw, JsonObject::class.java)
                    val msg = err.get("message")?.asString
                        ?: err.get("error")?.asString
                        ?: "Registration failed."
                    AuthResult(success = false, errorMessage = msg)
                }
            }
        } catch (e: Exception) {
            AuthResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }


    fun getMe(accessToken: String): User? {
        return try {
            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/api/users/me")
                .get()
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    gson.fromJson(raw, User::class.java)
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getUserByEmail(email: String, accessToken: String): User? = getMe(accessToken)
}
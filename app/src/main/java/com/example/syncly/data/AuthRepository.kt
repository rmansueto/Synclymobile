package com.example.syncly.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository {

    private val client = SupabaseClient
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

    fun login(email: String, password: String): AuthResult {
        return try {
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }

            val request = client.newRequestBuilder()
                .url("${client.getBaseUrl()}/auth/v1/token?grant_type=password")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json        = gson.fromJson(raw, JsonObject::class.java)
                    val token       = json.get("access_token").asString
                    val user        = parseUserFromAuth(json.getAsJsonObject("user"))
                    AuthResult(success = true, accessToken = token, user = user)
                } else {
                    val err = gson.fromJson(raw, JsonObject::class.java)
                    val msg = err.get("error_description")?.asString ?: "Login failed."
                    AuthResult(success = false, errorMessage = msg)
                }
            }
        } catch (e: Exception) {
            AuthResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register(email: String, password: String, fullName: String): AuthResult {
        return try {
            // full_name and photo_url go into user_metadata on signup.
            // Supabase stores these in auth.users and can be synced to
            // your public `users` table via a database trigger/function.
            val meta = JsonObject().apply {
                addProperty("full_name", fullName)
                addProperty("photo_url", "")
            }
            val body = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
                add("data", meta)
            }

            val request = client.newRequestBuilder()
                .url("${client.getBaseUrl()}/auth/v1/signup")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json  = gson.fromJson(raw, JsonObject::class.java)
                    val token = json.get("access_token")?.asString
                    val user  = parseUserFromAuth(
                        json.getAsJsonObject("user") ?: json
                    )
                    AuthResult(success = true, accessToken = token, user = user)
                } else {
                    val err = gson.fromJson(raw, JsonObject::class.java)
                    val msg = err.get("msg")?.asString ?: "Registration failed."
                    AuthResult(success = false, errorMessage = msg)
                }
            }
        } catch (e: Exception) {
            AuthResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }

    // ── Fetch user row from your public `users` table ─────────────────────────
    // Requires a `users` table in Supabase with columns:
    // id (uuid), email, full_name, photo_url, created_at

    fun getUserById(userId: String, accessToken: String): User? {
        return try {
            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/rest/v1/users?id=eq.$userId&select=*")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    gson.fromJson(raw, Array<User>::class.java).firstOrNull()
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ── Parse Supabase Auth user response → User ──────────────────────────────
    // Supabase Auth response structure:
    // { id, email, created_at, user_metadata: { full_name, photo_url } }

    private fun parseUserFromAuth(json: JsonObject?): User? {
        if (json == null) return null
        val meta = json.getAsJsonObject("user_metadata")
        return User(
            id        = json.get("id")?.asString,
            email     = json.get("email")?.asString,
            fullName  = meta?.get("full_name")?.asString,
            photoUrl  = meta?.get("photo_url")?.asString,
            createdAt = json.get("created_at")?.asString
        )
    }
}
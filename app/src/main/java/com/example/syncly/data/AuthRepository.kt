package com.example.syncly.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    data class AvailabilityResult(
        val success: Boolean,
        val data: List<Availability> = emptyList(),
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
                .url("${client.getBaseUrl()}/api/auth/login")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json  = gson.fromJson(raw, JsonObject::class.java)
                    val token = json.get("token").asString
                    val user  = getMe(token)
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

    // ── Register ──────────────────────────────────────────────────────────────

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
                    val user  = if (token != null) getMe(token) else null
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

    // ── GET /api/users/me ─────────────────────────────────────────────────────

    fun getMe(accessToken: String): User? {
        return try {
            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/api/users/me")
                .get()
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) gson.fromJson(raw, User::class.java) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    // ── PUT /api/users/me (multipart) ─────────────────────────────────────────
    // Matches your Spring Boot: fullName, newPassword, photo (MultipartFile)

    fun updateProfile(
        accessToken: String,
        fullName: String,
        newPassword: String?,
        photoFile: File?
    ): AuthResult {
        return try {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("fullName", fullName)
            if (!newPassword.isNullOrBlank()) {
                builder.addFormDataPart("newPassword", newPassword)
            }
            if (photoFile != null && photoFile.exists()) {
                val mediaType = "image/*".toMediaType()
                builder.addFormDataPart(
                    "photo",
                    photoFile.name,
                    photoFile.asRequestBody(mediaType)
                )
            }

            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/api/users/me")
                .put(builder.build())
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val user = gson.fromJson(raw, User::class.java)
                    AuthResult(success = true, user = user)
                } else {
                    AuthResult(success = false, errorMessage = "Failed to update profile.")
                }
            }
        } catch (e: Exception) {
            AuthResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }

    // ── GET /api/availability ─────────────────────────────────────────────────

    fun getAvailability(accessToken: String): AvailabilityResult {
        return try {
            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/api/availability")
                .get()
                .build()

            client.getClient().newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val list = gson.fromJson(raw, Array<Availability>::class.java).toList()
                    AvailabilityResult(success = true, data = list)
                } else {
                    AvailabilityResult(success = false, errorMessage = "Failed to load availability.")
                }
            }
        } catch (e: Exception) {
            AvailabilityResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }

    // ── POST /api/availability/bulk ───────────────────────────────────────────

    fun saveAvailability(accessToken: String, list: List<Availability>): AvailabilityResult {
        return try {
            val body = gson.toJson(list)

            val request = client.newAuthenticatedRequestBuilder(accessToken)
                .url("${client.getBaseUrl()}/api/availability/bulk")
                .post(body.toRequestBody(JSON))
                .build()

            client.getClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    AvailabilityResult(success = true)
                } else {
                    AvailabilityResult(success = false, errorMessage = "Failed to save availability.")
                }
            }
        } catch (e: Exception) {
            AvailabilityResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }

    fun getUserByEmail(email: String, accessToken: String): User? = getMe(accessToken)
}
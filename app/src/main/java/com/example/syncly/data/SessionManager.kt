package com.example.syncly.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("syncly_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN     = "access_token"
        private const val KEY_USER_ID   = "user_id"
        private const val KEY_EMAIL     = "email"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_PHOTO_URL = "photo_url"
    }

    fun saveSession(token: String, user: User) {
        prefs.edit()
            .putString(KEY_TOKEN,     token)
            .putLong(KEY_USER_ID,     user.id ?: -1L)
            .putString(KEY_EMAIL,     user.email)
            .putString(KEY_FULL_NAME, user.fullName)
            .putString(KEY_PHOTO_URL, user.photoUrl)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_TOKEN,     null)
    fun getUserId(): Long?        = prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    fun getEmail(): String?       = prefs.getString(KEY_EMAIL,     null)
    fun getFullName(): String?    = prefs.getString(KEY_FULL_NAME, null)
    fun getPhotoUrl(): String?    = prefs.getString(KEY_PHOTO_URL, null)
    fun isLoggedIn(): Boolean     = getAccessToken() != null

    fun getCachedUser(): User = User(
        id       = getUserId(),
        email    = getEmail(),
        fullName = getFullName(),
        photoUrl = getPhotoUrl()
    )

    fun clearSession() = prefs.edit().clear().apply()
}
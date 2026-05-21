package com.example.syncly.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("syncly_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN      = "access_token"
        private const val KEY_USER_ID    = "user_id"
        private const val KEY_EMAIL      = "email"
        private const val KEY_FULL_NAME  = "full_name"
        private const val KEY_PHOTO_URL  = "photo_url"
        private const val KEY_CREATED_AT = "created_at"
    }

    fun saveSession(token: String, user: User) {
        prefs.edit()
            .putString(KEY_TOKEN,      token)
            .putString(KEY_USER_ID,    user.id)
            .putString(KEY_EMAIL,      user.email)
            .putString(KEY_FULL_NAME,  user.fullName)
            .putString(KEY_PHOTO_URL,  user.photoUrl)
            .putString(KEY_CREATED_AT, user.createdAt)
            .apply()
    }

    fun getAccessToken(): String?  = prefs.getString(KEY_TOKEN,     null)
    fun getUserId(): String?       = prefs.getString(KEY_USER_ID,   null)
    fun getEmail(): String?        = prefs.getString(KEY_EMAIL,     null)
    fun getFullName(): String?     = prefs.getString(KEY_FULL_NAME, null)
    fun getPhotoUrl(): String?     = prefs.getString(KEY_PHOTO_URL, null)
    fun getCreatedAt(): String?    = prefs.getString(KEY_CREATED_AT,null)
    fun isLoggedIn(): Boolean      = getAccessToken() != null

    fun getCachedUser(): User = User(
        id        = getUserId(),
        email     = getEmail(),
        fullName  = getFullName(),
        photoUrl  = getPhotoUrl(),
        createdAt = getCreatedAt()
    )

    fun clearSession() = prefs.edit().clear().apply()
}
package com.example.syncly.data

import com.google.gson.annotations.SerializedName

data class User(

    // Matches Spring Boot User.java `Long id`
    @SerializedName("id")
    val id: Long? = null,

    // Matches `String email`
    @SerializedName("email")
    val email: String? = null,

    // Matches `String fullName`
    // Spring Boot serializes camelCase as-is by default
    @SerializedName("fullName")
    val fullName: String? = null,

    // Matches `String photoUrl`
    @SerializedName("photoUrl")
    val photoUrl: String? = null

    // password is intentionally excluded — never store or use it on Android
)
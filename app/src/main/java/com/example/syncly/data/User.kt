package com.example.syncly.data

import com.google.gson.annotations.SerializedName

data class User(

    // Supabase Auth uses a UUID string for `id`, not a Long like Spring Boot.
    // The `users` table in Supabase maps this as `id uuid` (primary key).
    @SerializedName("id")
    val id: String? = null,

    // Matches: @Column(nullable = false) String email
    @SerializedName("email")
    val email: String? = null,

    // Password is NEVER stored or returned by Supabase Auth REST responses.
    // Authentication is handled via Supabase Auth — no raw password field.

    // Matches: String fullName  (Supabase column: full_name)
    @SerializedName("full_name")
    val fullName: String? = null,

    // Matches: String photoUrl  (Supabase column: photo_url)
    @SerializedName("photo_url")
    val photoUrl: String? = null,

    // Supabase automatically provides created_at on all auth users
    @SerializedName("created_at")
    val createdAt: String? = null
)
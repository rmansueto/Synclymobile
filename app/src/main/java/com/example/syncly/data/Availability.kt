package com.example.syncly.data

import com.google.gson.annotations.SerializedName

data class Availability(

    @SerializedName("id")
    val id: Long? = null,

    // 0 = Sunday, 6 = Saturday — matches your Spring Boot entity
    @SerializedName("dayOfWeek")
    val dayOfWeek: Int = 0,

    // Format: "HH:mm:ss" from Spring Boot LocalTime
    @SerializedName("startTime")
    val startTime: String? = "09:00",

    // Format: "HH:mm:ss" from Spring Boot LocalTime
    @SerializedName("endTime")
    val endTime: String? = "17:00",

    @SerializedName("timezone")
    val timezone: String? = "UTC",

    // organizerId is set by backend from Principal — not sent by Android
    @SerializedName("organizerId")
    val organizerId: Long? = null
)
package com.example.syncly.screens.availability

import com.example.syncly.data.AuthRepository
import com.example.syncly.data.Availability

class AvailabilityModel {

    private val repository = AuthRepository()

    fun getAvailability(accessToken: String): AuthRepository.AvailabilityResult =
        repository.getAvailability(accessToken)

    fun saveAvailability(
        accessToken: String,
        list: List<Availability>
    ): AuthRepository.AvailabilityResult =
        repository.saveAvailability(accessToken, list)
}
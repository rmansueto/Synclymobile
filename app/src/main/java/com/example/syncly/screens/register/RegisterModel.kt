package com.example.syncly.screens.register

import com.example.syncly.data.AuthRepository

class RegisterModel {

    private val repository = AuthRepository()

    fun register(
        email: String,
        password: String,
        fullName: String
    ): AuthRepository.AuthResult =
        repository.register(email, password, fullName)
}
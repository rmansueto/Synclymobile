package com.example.syncly.screens.login

import com.example.syncly.data.AuthRepository

class LoginModel {

    private val repository = AuthRepository()

    fun login(email: String, password: String): AuthRepository.AuthResult =
        repository.login(email, password)
}
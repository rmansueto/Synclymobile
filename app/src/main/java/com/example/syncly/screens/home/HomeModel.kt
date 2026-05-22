package com.example.syncly.screens.home

import com.example.syncly.data.AuthRepository
import com.example.syncly.data.User

class HomeModel {

    private val repository = AuthRepository()


    fun fetchUser(accessToken: String): User? =
        repository.getMe(accessToken)
}
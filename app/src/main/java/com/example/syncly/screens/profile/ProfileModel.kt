package com.example.syncly.screens.profile

import com.example.syncly.data.AuthRepository
import com.example.syncly.data.User
import java.io.File

class ProfileModel {

    private val repository = AuthRepository()

    fun getMe(accessToken: String): User? =
        repository.getMe(accessToken)

    fun updateProfile(
        accessToken: String,
        fullName: String,
        newPassword: String?,
        photoFile: File?
    ): AuthRepository.AuthResult =
        repository.updateProfile(accessToken, fullName, newPassword, photoFile)
}
package com.example.syncly.screens.profile

import com.example.syncly.data.User
import java.io.File

interface ProfileContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayUser(user: User)
        fun onUpdateSuccess(user: User)
        fun onUpdateError(message: String)
        fun navigateBack()
    }

    interface Presenter {
        fun loadProfile()
        fun updateProfile(
            fullName: String,
            newPassword: String?,
            confirmPassword: String?,
            photoFile: File?
        )
        fun onBackClicked()
        fun detachView()
    }
}
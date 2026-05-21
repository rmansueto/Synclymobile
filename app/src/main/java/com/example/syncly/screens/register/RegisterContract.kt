package com.example.syncly.screens.register;

import com.example.syncly.data.User;

interface RegisterContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun onRegisterSuccess(user: User)
        fun onRegisterError(message: String)
        fun navigateToHome()
        fun navigateToLogin()
    }

    interface Presenter {
        fun register(
            email: String,
            password: String,
            confirmPassword: String,
            fullName: String
        )
        fun onLoginClicked()
        fun detachView()
    }
}
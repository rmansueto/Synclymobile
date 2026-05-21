package com.example.syncly.screens.login

import com.example.syncly.data.User

interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun onLoginSuccess(user: User)
        fun onLoginError(message: String)
        fun navigateToHome()
        fun navigateToRegister()
    }

    interface Presenter {
        fun login(email: String, password: String)
        fun onRegisterClicked()
        fun detachView()
    }
}
package com.example.syncly.screens.home

import com.example.syncly.data.User

interface HomeContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayUserInfo(user: User)
        fun onLoadError(message: String)
        fun navigateToLogin()
    }

    interface Presenter {
        fun loadUserData()
        fun logout()
        fun detachView()
    }
}
package com.example.syncly.screens.home

import android.os.Handler
import android.os.Looper
import com.example.syncly.data.SessionManager

class HomePresenter(
    private var view: HomeContract.View?,
    private val model: HomeModel,
    private val session: SessionManager
) : HomeContract.Presenter {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun loadUserData() {
        val token = session.getAccessToken()

        if (token == null) {
            view?.navigateToLogin()
            return
        }

        // Show cached data immediately
        view?.displayUserInfo(session.getCachedUser())

        // Refresh from Spring Boot backend in background
        view?.showLoading()
        Thread {
            val freshUser = model.fetchUser(token)  // no userId needed anymore
            mainHandler.post {
                view?.hideLoading()
                if (freshUser != null) {
                    session.saveSession(token, freshUser)
                    view?.displayUserInfo(freshUser)
                } else {
                    view?.onLoadError("Could not refresh profile.")
                }
            }
        }.start()
    }

    override fun logout() {
        session.clearSession()
        view?.navigateToLogin()
    }

    override fun detachView() {
        view = null
    }
}
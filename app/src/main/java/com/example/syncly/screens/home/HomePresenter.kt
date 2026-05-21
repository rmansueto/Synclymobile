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
        val token  = session.getAccessToken()
        val userId = session.getUserId()

        if (token == null || userId == null) {
            view?.navigateToLogin()
            return
        }

        // Show cached session data immediately — no waiting for network
        view?.displayUserInfo(session.getCachedUser())

        // Then silently refresh from Supabase in background
        view?.showLoading()
        Thread {
            val freshUser = model.fetchUser(userId, token)
            mainHandler.post {
                view?.hideLoading()
                if (freshUser != null) {
                    // Update session cache with fresh data
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
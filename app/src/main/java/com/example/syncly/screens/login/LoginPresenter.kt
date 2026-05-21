package com.example.syncly.screens.login

import android.os.Handler
import android.os.Looper
import com.example.syncly.data.SessionManager
import com.example.syncly.utils.isValidEmail

class LoginPresenter(
    private var view: LoginContract.View?,
    private val model: LoginModel,
    private val session: SessionManager
) : LoginContract.Presenter {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun login(email: String, password: String) {
        when {
            email.isBlank()        -> { view?.onLoginError("Email is required."); return }
            !email.isValidEmail()  -> { view?.onLoginError("Enter a valid email."); return }
            password.isBlank()     -> { view?.onLoginError("Password is required."); return }
            password.length < 6   -> { view?.onLoginError("Password must be at least 6 characters."); return }
        }

        view?.showLoading()

        Thread {
            val result = model.login(email.trim(), password)
            mainHandler.post {
                view?.hideLoading()
                if (result.success && result.user != null && result.accessToken != null) {
                    session.saveSession(result.accessToken, result.user)
                    view?.onLoginSuccess(result.user)
                    view?.navigateToHome()
                } else {
                    view?.onLoginError(result.errorMessage ?: "Login failed.")
                }
            }
        }.start()
    }

    override fun onRegisterClicked() {
        view?.navigateToRegister()
    }

    override fun detachView() {
        view = null
    }
}
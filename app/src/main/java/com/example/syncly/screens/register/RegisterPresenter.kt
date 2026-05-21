package com.example.syncly.screens.register

import android.os.Handler
import android.os.Looper
import com.example.syncly.data.SessionManager
import com.example.syncly.utils.isValidEmail

class RegisterPresenter(
    private var view: RegisterContract.View?,
    private val model: RegisterModel,
    private val session: SessionManager
) : RegisterContract.Presenter {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun register(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String
    ) {
        when {
            fullName.isBlank()          -> { view?.onRegisterError("Full name is required."); return }
            email.isBlank()             -> { view?.onRegisterError("Email is required."); return }
            !email.isValidEmail()       -> { view?.onRegisterError("Enter a valid email."); return }
            password.isBlank()          -> { view?.onRegisterError("Password is required."); return }
            password.length < 6        -> { view?.onRegisterError("Password must be at least 6 characters."); return }
            password != confirmPassword -> { view?.onRegisterError("Passwords do not match."); return }
        }

        view?.showLoading()

        Thread {
            val result = model.register(email.trim(), password, fullName.trim())
            mainHandler.post {
                view?.hideLoading()
                if (result.success && result.user != null) {
                    result.accessToken?.let { token ->
                        session.saveSession(token, result.user)
                    }
                    view?.onRegisterSuccess(result.user)
                    view?.navigateToHome()
                } else {
                    view?.onRegisterError(result.errorMessage ?: "Registration failed.")
                }
            }
        }.start()
    }

    override fun onLoginClicked() {
        view?.navigateToLogin()
    }

    override fun detachView() {
        view = null
    }
}
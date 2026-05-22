package com.example.syncly.screens.profile

import android.os.Handler
import android.os.Looper
import com.example.syncly.data.SessionManager
import com.example.syncly.utils.isValidPassword
import java.io.File

class ProfilePresenter(
    private var view: ProfileContract.View?,
    private val model: ProfileModel,
    private val session: SessionManager
) : ProfileContract.Presenter {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun loadProfile() {
        val token = session.getAccessToken() ?: return

        // Show cached immediately
        view?.displayUser(session.getCachedUser())

        Thread {
            val user = model.getMe(token)
            mainHandler.post {
                if (user != null) {
                    session.saveSession(token, user)
                    view?.displayUser(user)
                }
            }
        }.start()
    }

    override fun updateProfile(
        fullName: String,
        newPassword: String?,
        confirmPassword: String?,
        photoFile: File?
    ) {
        // Validate
        when {
            fullName.isBlank() -> {
                view?.onUpdateError("Full name cannot be empty.")
                return
            }
            !newPassword.isNullOrBlank() && !confirmPassword.isNullOrBlank() &&
                    newPassword != confirmPassword -> {
                view?.onUpdateError("Passwords do not match.")
                return
            }
            !newPassword.isNullOrBlank() && !newPassword.isValidPassword() -> {
                view?.onUpdateError("Password must be at least 6 characters.")
                return
            }
        }

        val token = session.getAccessToken() ?: run {
            view?.onUpdateError("Session expired. Please login again.")
            return
        }

        view?.showLoading()

        Thread {
            val result = model.updateProfile(
                accessToken = token,
                fullName    = fullName.trim(),
                newPassword = newPassword?.takeIf { it.isNotBlank() },
                photoFile   = photoFile
            )
            mainHandler.post {
                view?.hideLoading()
                if (result.success && result.user != null) {
                    session.saveSession(token, result.user)
                    view?.onUpdateSuccess(result.user)
                } else {
                    view?.onUpdateError(result.errorMessage ?: "Update failed.")
                }
            }
        }.start()
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun detachView() {
        view = null
    }
}
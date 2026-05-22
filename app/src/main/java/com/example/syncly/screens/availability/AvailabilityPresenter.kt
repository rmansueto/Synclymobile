package com.example.syncly.screens.availability

import android.os.Handler
import android.os.Looper
import com.example.syncly.data.Availability
import com.example.syncly.data.SessionManager

class AvailabilityPresenter(
    private var view: AvailabilityContract.View?,
    private val model: AvailabilityModel,
    private val session: SessionManager
) : AvailabilityContract.Presenter {

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun loadAvailability() {
        val token = session.getAccessToken() ?: run {
            view?.onError("Session expired.")
            return
        }

        view?.showLoading()

        Thread {
            val result = model.getAvailability(token)
            mainHandler.post {
                view?.hideLoading()
                if (result.success) {
                    // Group by dayOfWeek (0=Sun to 6=Sat) — same as your web app
                    val weekly = (0..6).associateWith { day ->
                        result.data.filter { it.dayOfWeek == day }
                    }
                    view?.displayAvailability(weekly)
                } else {
                    view?.onError(result.errorMessage ?: "Failed to load availability.")
                }
            }
        }.start()
    }

    override fun saveAvailability(weekly: Map<Int, List<Availability>>) {
        val token = session.getAccessToken() ?: run {
            view?.onError("Session expired.")
            return
        }

        // Flatten map back to list — same as your web app's payload
        val list = weekly.values.flatten()

        view?.showLoading()

        Thread {
            val result = model.saveAvailability(token, list)
            mainHandler.post {
                view?.hideLoading()
                if (result.success) {
                    view?.onSaveSuccess()
                } else {
                    view?.onError(result.errorMessage ?: "Failed to save.")
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
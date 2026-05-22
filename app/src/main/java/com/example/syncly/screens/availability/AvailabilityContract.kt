package com.example.syncly.screens.availability

import com.example.syncly.data.Availability

interface AvailabilityContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayAvailability(weekly: Map<Int, List<Availability>>)
        fun onSaveSuccess()
        fun onError(message: String)
        fun navigateBack()
    }

    interface Presenter {
        fun loadAvailability()
        fun saveAvailability(weekly: Map<Int, List<Availability>>)
        fun onBackClicked()
        fun detachView()
    }
}
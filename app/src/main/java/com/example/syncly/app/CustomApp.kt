package com.example.syncly.app

import android.app.Application

class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // ApiClient uses a hardcoded base URL — no init needed
    }
}
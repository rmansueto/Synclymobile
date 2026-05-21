package com.example.syncly.app

import android.app.Application
import com.example.syncly.BuildConfig
import com.example.syncly.data.SupabaseClient

class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SupabaseClient.init(
            url     = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY
        )
    }
}
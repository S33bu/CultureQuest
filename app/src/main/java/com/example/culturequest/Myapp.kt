package com.example.culturequest

import android.app.Application
import android.content.Context

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object { // warns about memeory leak but should be fine because Application lives as long as the app
        lateinit var context: Context
            private set
    }
}

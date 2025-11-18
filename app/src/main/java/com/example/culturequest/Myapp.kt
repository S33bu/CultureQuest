package com.example.culturequest

import android.app.Application
import android.content.Context
import com.example.culturequest.data.SettingsDataStore

class MyApp : Application() {

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        settingsDataStore = SettingsDataStore(this)
    }

    fun getSettingsDataStore(): SettingsDataStore {
        return settingsDataStore
    }

    companion object { // warns about memory leak but should be fine because Application lives as long as the app
        lateinit var context: Context
            private set

        fun from(context: Context): MyApp {
            return context.applicationContext as MyApp
        }
    }
}

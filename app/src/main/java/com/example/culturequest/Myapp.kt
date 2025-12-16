package com.example.culturequest

import android.app.Application
import android.content.Context
import com.example.culturequest.data.SettingsDataStore
import androidx.room.Room
import com.example.culturequest.data.AppDatabase


class MyApp : Application() {

    private lateinit var settingsDataStore: SettingsDataStore


    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "culturequest-db"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        settingsDataStore = SettingsDataStore(this)
    }

    fun getSettingsDataStore(): SettingsDataStore {
        return settingsDataStore
    }

    companion object { // warns about memory leak but should be fine because Application lives as long as the app

        lateinit var instance: MyApp
            private set

        lateinit var context: Context
            private set

        fun from(context: Context): MyApp {
            return context.applicationContext as MyApp
        }
    }
}

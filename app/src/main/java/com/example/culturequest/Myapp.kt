package com.example.culturequest

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.culturequest.data.AppDatabase
import com.example.culturequest.data.SettingsDataStore

/**
 * The main [Application] class for the CultureQuest app.
 *
 * This class is responsible for initializing global components and providing
 * application-wide access to them, such as the database and settings DataStore.
 */
class MyApp : Application() {
    private lateinit var settingsDataStore: SettingsDataStore

    /**
     * The singleton instance of the Room [AppDatabase].
     *
     * It is lazily initialized to ensure it is only created when first needed.
     */
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "culturequest-db",
        ).build()
    }

    /**
     * Initializes the application instance, context, and data stores when the app is created.
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        settingsDataStore = SettingsDataStore(this)
    }

    /**
     * Returns the singleton instance of [SettingsDataStore].
     *
     * @return The application's [SettingsDataStore].
     */
    fun getSettingsDataStore(): SettingsDataStore = settingsDataStore

    /**
     * Provides global access to the application context and instance.
     */
    companion object { // warns about memory leak but should be fine because Application lives as long as the app

        /**
         * The singleton instance of the [MyApp] application.
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MyApp
            private set

        /**
         * The global application [Context].
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        /**
         * A helper function to retrieve the [MyApp] instance from any [Context].
         *
         * @param context The context from which to get the application instance.
         * @return The singleton [MyApp] instance.
         */
        fun from(context: Context): MyApp = context.applicationContext as MyApp
    }
}

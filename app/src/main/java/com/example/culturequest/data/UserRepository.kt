package com.example.culturequest.data

import com.example.culturequest.MyApp

/**
 * A repository for managing user data, providing a clean API for data access.
 *
 * @property database The application's [AppDatabase] instance.
 */
class UserRepository(
    private val database: AppDatabase = MyApp.instance.database,
) {
    private val userDao = database.userDao()

    /**
     * Returns a [Flow] that emits the [UserProfile] for the given UID whenever it changes.
     *
     * @param uid The Firebase UID of the user to observe.
     * @return A [Flow] of the user's profile.
     */
    fun getUserProfile(uid: String) = userDao.getUserFlow(uid)

    /**
     * Retrieves the [UserProfile] for the given UID.
     *
     * @param uid The Firebase UID of the user to retrieve.
     * @return The [UserProfile] if found, otherwise null.
     */
    suspend fun getUser(uid: String) = userDao.getUser(uid)

    /**
     * Saves the user's progress to the local database.
     *
     * @param profile The [UserProfile] to be saved.
     */
    suspend fun saveLocalProgress(profile: UserProfile) = userDao.insertUser(profile)
}

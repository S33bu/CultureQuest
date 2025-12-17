package com.example.culturequest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the [UserProfile] table.
 *
 * Provides methods to read and write user profile data.
 */
@Dao
interface UserDao {
    /**
     * Inserts a [UserProfile] into the database. If the user already exists, it will be replaced.
     *
     * @param user The user profile to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    /**
     * Retrieves the [UserProfile] for the given Firebase UID.
     *
     * @param uid The Firebase UID of the user to retrieve.
     * @return The [UserProfile] if found, otherwise null.
     */
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    suspend fun getUser(uid: String): UserProfile?

    /**
     * Returns a [Flow] for observing changes to the [UserProfile] for a given UID in real-time.
     *
     * @param uid The Firebase UID of the user to observe.
     * @return A [Flow] that emits the [UserProfile] whenever it changes.
     */
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserFlow(uid: String): Flow<UserProfile?>

    /**
     * Updates an existing [UserProfile] in the database.
     *
     * @param user The user profile to be updated.
     */
    @Update
    suspend fun updateUser(user: UserProfile)
}

// Data Access Object (DAO) for the UserProfile table
// Contains methods to read and write user profile data.
package com.example.culturequest.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Inserts a user into the database. If the user already exists, replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    // Retrieve the user profile for the given Firebase UID (if any). Suspends until done.
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    suspend fun getUser(uid: String): UserProfile?

    // Returns a Flow for observing changes to the user profile for a given UID in real-time.
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserFlow(uid: String): Flow<UserProfile?>

    // Updates an existing user in the database
    @Update
    suspend fun updateUser(user: UserProfile)
}

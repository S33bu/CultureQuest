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

    // Retrieve the first user in the database (if any). Suspends until done.
    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUser(): UserProfile?

    // Returns a Flow for observing changes to the user profile in real-time.
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserFlow(): Flow<UserProfile?>

    // Updates an existing user in the database
    @Update
    suspend fun updateUser(user: UserProfile)
}

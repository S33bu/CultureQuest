package com.example.culturequest.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUser(): UserProfile?

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserFlow(): Flow<UserProfile?>

    @Update
    suspend fun updateUser(user: UserProfile)
}

package com.example.culturequest.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfile)

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUser(): UserProfile?

    @Update
    suspend fun updateUser(user: UserProfile)
}
// Represents a user profile in the app with a username and a score.
// Stored in the "user_profile" table in the Room database.
package com.example.culturequest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String, // Name of the user
    val score: Int, // User current score
    val bestScore: Int = 0 // all-time highscore
)

// Room database class that ties together UserProfile and QuizQuestion tables
package com.example.culturequest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserProfile::class, QuizQuestion::class], // Database tables
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    // Provides access to user-related queries
    abstract fun userDao(): UserDao

    // Provides access to quiz question queries
    abstract fun questionDao(): QuestionDao
}

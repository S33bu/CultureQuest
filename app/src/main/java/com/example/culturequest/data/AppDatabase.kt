// Room database class that ties together UserProfile and QuizQuestion tables
package com.example.culturequest.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The main Room database for the CultureQuest application.
 *
 * This database acts as the primary local storage for the application,
 * maintaining tables for user profiles and quiz content. It follows the
 * Singleton pattern when instantiated through the database builder.
 *
 * Tables:
 * - [UserProfile]: Stores persistent user data including scores and preferences.
 * - [QuizQuestion]: Stores the library of cultural questions used during gameplay.
 */
@Database(
    entities = [UserProfile::class, QuizQuestion::class], // Database tables
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to the Data Access Object (DAO) for user profile operations.
     *
     * @return An instance of [UserDao].
     */
    abstract fun userDao(): UserDao

    /**
     * Provides access to the Data Access Object (DAO) for quiz question operations.
     *
     * @return An instance of [QuestionDao].
     */
    abstract fun questionDao(): QuestionDao
}

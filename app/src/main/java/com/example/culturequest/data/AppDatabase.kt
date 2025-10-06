package com.example.culturequest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserProfile::class, QuizQuestion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
}
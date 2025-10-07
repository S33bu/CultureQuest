// Represents a single quiz question in the app.
// Stored in the "quiz_questions" table in the Room database.
package com.example.culturequest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String, // The question text
    val correctAnswer: String, // The correct answer string
    val imageResId: Int  // Drawable resource ID for the image associated with the question
)

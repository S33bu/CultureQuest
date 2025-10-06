package com.example.culturequest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val correctAnswer: String,
    val imageResId: Int  // store drawable reference (example: R.drawable.australia)
)
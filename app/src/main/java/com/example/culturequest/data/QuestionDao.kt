package com.example.culturequest.data

import androidx.room.*

@Dao
interface QuestionDao {
    @Query("SELECT * FROM quiz_questions")
    suspend fun getAllQuestions(): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuizQuestion>)

    @Query("DELETE FROM quiz_questions")
    suspend fun clearAll()
}
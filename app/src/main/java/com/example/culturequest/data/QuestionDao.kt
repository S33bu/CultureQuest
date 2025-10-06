package com.example.culturequest.data

import androidx.room.*

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion)

    @Query("SELECT * FROM quiz_question")
    suspend fun getAllQuestions(): List<QuizQuestion>

    @Query("SELECT * FROM quiz_question ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): QuizQuestion?
}
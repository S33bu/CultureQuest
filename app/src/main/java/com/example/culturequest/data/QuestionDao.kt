// Data Access Object (DAO) for the QuizQuestion table
// Contains methods to query, insert, and clear quiz questions.
package com.example.culturequest.data

import androidx.room.*

@Dao
interface QuestionDao {
    // Retrieve all quiz questions from the database
    @Query("SELECT * FROM quiz_questions")
    suspend fun getAllQuestions(): List<QuizQuestion>

    // Insert a single question; replace it if it already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion)

    // Insert a list of questions; replace any existing ones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuizQuestion>)

    // Deletes all quiz questions from the database
    @Query("DELETE FROM quiz_questions")
    suspend fun clearAll()
}

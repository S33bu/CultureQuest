package com.example.culturequest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for the [QuizQuestion] table.
 *
 * Provides methods to query, insert, and clear quiz questions from the database.
 */
@Dao
interface QuestionDao {
    /**
     * Retrieves all quiz questions from the database.
     *
     * @return A list of all [QuizQuestion] objects.
     */
    @Query("SELECT * FROM quiz_questions")
    suspend fun getAllQuestions(): List<QuizQuestion>

    /**
     * Inserts a single [QuizQuestion] into the database, replacing it if it already exists.
     *
     * @param question The question to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion)

    /**
     * Inserts a list of [QuizQuestion] objects into the database, replacing any existing ones.
     *
     * @param questions The list of questions to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuizQuestion>)

    /**
     * Deletes all quiz questions from the database.
     */
    @Query("DELETE FROM quiz_questions")
    suspend fun clearAll()
}

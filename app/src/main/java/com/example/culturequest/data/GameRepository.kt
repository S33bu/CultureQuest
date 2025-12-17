package com.example.culturequest.data

import com.example.culturequest.MyApp

/**
 * A repository for managing game data, such as quiz questions.
 *
 * @property database The application's [AppDatabase] instance.
 */
class GameRepository(
    private val database: AppDatabase = MyApp.instance.database,
) {
    private val questionDao = database.questionDao()

    /**
     * Retrieves all quiz questions from the database.
     *
     * @return A list of all [QuizQuestion] objects.
     */
    suspend fun getAllQuestions(): List<QuizQuestion> = questionDao.getAllQuestions()

    /**
     * Refreshes the quiz questions in the database by clearing the existing ones and inserting a new list.
     *
     * @param questions The new list of questions to be inserted.
     */
    suspend fun refreshQuestions(questions: List<QuizQuestion>) {
        questionDao.clearAll()
        questionDao.insertAll(questions)
    }
}

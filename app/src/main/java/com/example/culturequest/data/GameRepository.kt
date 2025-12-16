package com.example.culturequest.data

import com.example.culturequest.MyApp

class GameRepository(
    private val database: AppDatabase = MyApp.instance.database,
) {
    private val questionDao = database.questionDao()

    suspend fun getAllQuestions(): List<QuizQuestion> = questionDao.getAllQuestions()

    suspend fun refreshQuestions(questions: List<QuizQuestion>) {
        questionDao.clearAll()
        questionDao.insertAll(questions)
    }
}

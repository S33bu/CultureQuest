package com.example.culturequest.data
import kotlinx.coroutines.flow.Flow
import com.example.culturequest.MyApp

class UserRepository(private val database: AppDatabase = MyApp.instance.database) {
    private val userDao = database.userDao()

    fun getUserProfile(uid: String) = userDao.getUserFlow(uid)
    suspend fun getUser(uid: String) = userDao.getUser(uid)
    suspend fun saveLocalProgress(profile: UserProfile) = userDao.insertUser(profile)
}
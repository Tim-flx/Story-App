package com.felixtp.storyappsub2.data.di

import android.content.Context
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.local.UserPreference
import com.felixtp.storyappsub2.data.local.dataStore
import com.felixtp.storyappsub2.data.local.room.StoryDatabase
import com.felixtp.storyappsub2.data.remote.retrofit.ApiConfig

object UserRepositoryModule {
    fun getRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(dataStore = context.dataStore)
        val storyDatabase = StoryDatabase.getDatabase(context)

        return UserRepository(apiService, userPreference, storyDatabase)
    }
}

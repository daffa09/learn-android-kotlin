package com.dicoding.learn.di

import android.content.Context
import com.dicoding.learn.data.local.database.StoryDatabase
import com.dicoding.learn.data.model.UserPreference
import com.dicoding.learn.data.model.dataStore
import com.dicoding.learn.data.repository.AuthRepository
import com.dicoding.learn.data.repository.StoryRepository
import com.dicoding.learn.data.remote.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val storyDatabase = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(storyDatabase, apiService)
    }
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return AuthRepository.getInstance(apiService, pref)
    }
}
package com.dicoding.learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.learn.data.model.UserModel
import com.dicoding.learn.data.repository.AuthRepository
import com.dicoding.learn.data.repository.StoryRepository
import com.dicoding.learn.data.model.ListStoryItem
import kotlinx.coroutines.launch


class MainViewModel(
    storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val story : LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStory().cachedIn(viewModelScope)

    fun getSession() : LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }
}
package com.dicoding.learn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.dicoding.learn.data.repository.StoryRepository

class MapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation(location = 1)
}
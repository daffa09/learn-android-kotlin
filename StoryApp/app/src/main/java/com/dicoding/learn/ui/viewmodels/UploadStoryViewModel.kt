package com.dicoding.learn.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.learn.data.repository.StoryRepository
import java.io.File

class UploadStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel(){
    private var _curImage = MutableLiveData<Uri?>()
    val curImage : MutableLiveData<Uri?> = _curImage

    fun setCurImage(uri : Uri?) { _curImage.value = uri }

    fun uploadStory(file : File, description : String, lat : Float?, lon : Float?) = storyRepository.addStory(file, description, lat, lon)
}
package com.felixtp.storyappsub2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.remote.response.Story

class DetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    val isLoading: LiveData<Boolean> = userRepository.isLoading
    val isError: LiveData<Boolean> = userRepository.isError

    fun getDetailStory(token: String, id: String): LiveData<Story> {
        return userRepository.getDetailStory(token, id)
    }

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}

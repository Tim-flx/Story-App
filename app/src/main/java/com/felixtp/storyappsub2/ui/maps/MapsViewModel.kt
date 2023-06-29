package com.felixtp.storyappsub2.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.remote.response.ListStoryItem

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getStoriesMap(token: String, enableLocation: Int): LiveData<List<ListStoryItem>> {
        return userRepository.getAllStoresMap(token, enableLocation)
    }

    fun getToken(): LiveData<String> = userRepository.getToken()
    fun isLoading(): LiveData<Boolean> = userRepository.isLoading
    fun isError(): LiveData<Boolean> = userRepository.isError
}
package com.felixtp.storyappsub2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    val isLoading: LiveData<Boolean> = userRepository.isLoading
    val isError: LiveData<Boolean> = userRepository.isError
    val isUpdated: LiveData<Boolean> = userRepository.isUpdate

    fun setLogout() {
        viewModelScope.launch {
            userRepository.setLogout()
        }
    }

    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> =
        userRepository.getStory(token).cachedIn(viewModelScope)

    fun getToken(): LiveData<String> = userRepository.getToken()
}

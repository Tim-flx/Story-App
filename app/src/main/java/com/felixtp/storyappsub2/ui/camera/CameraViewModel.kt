package com.felixtp.storyappsub2.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.remote.response.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CameraViewModel(private val userRepository: UserRepository) : ViewModel() {

    val message: LiveData<BaseResponse> = userRepository.message
    val isLoading: LiveData<Boolean> = userRepository.isLoading
    val isError: LiveData<Boolean> = userRepository.isError

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ) {
        userRepository.postStory(token, file, description, lat, lon)
    }

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}

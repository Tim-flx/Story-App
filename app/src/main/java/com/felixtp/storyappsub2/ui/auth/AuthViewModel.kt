package com.felixtp.storyappsub2.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.remote.response.BaseResponse
import com.felixtp.storyappsub2.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    val loginResponse: LiveData<LoginResponse> = userRepository.loginResponse
    val message: LiveData<BaseResponse> = userRepository.message
    val isLoading: LiveData<Boolean> = userRepository.isLoading
    val isError: LiveData<Boolean> = userRepository.isError
    val isSuccessRegister: LiveData<Boolean> = userRepository.isSuccessRegister
    val isSuccessLogin: LiveData<Boolean> = userRepository.isSuccessLogin
    val session: LiveData<Boolean> = userRepository.getSession()

    fun register(name: String, email: String, password: String) {
        userRepository.registerUser(name, email, password)
    }

    fun login(email: String, password: String) {
        userRepository.loginUser(email, password)
    }

    fun saveUser(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            userRepository.saveUser(token, isLogin)
        }
    }
}

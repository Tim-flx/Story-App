package com.felixtp.storyappsub2.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixtp.storyappsub2.data.UserRepository
import com.felixtp.storyappsub2.data.di.UserRepositoryModule
import com.felixtp.storyappsub2.ui.auth.AuthViewModel
import com.felixtp.storyappsub2.ui.camera.CameraViewModel
import com.felixtp.storyappsub2.ui.detail.DetailViewModel
import com.felixtp.storyappsub2.ui.main.MainViewModel
import com.felixtp.storyappsub2.ui.maps.MapsViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(UserRepositoryModule.getRepository(context))
                    .also { INSTANCE = it }
            }
        }
    }
}

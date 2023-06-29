package com.felixtp.storyappsub2.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.felixtp.storyappsub2.data.local.UserPreference
import com.felixtp.storyappsub2.data.local.room.StoryDatabase
import com.felixtp.storyappsub2.data.remote.response.BaseResponse
import com.felixtp.storyappsub2.data.remote.response.DetailStoryResponse
import com.felixtp.storyappsub2.data.remote.response.GetAllStoriesResponse
import com.felixtp.storyappsub2.data.remote.response.ListStoryItem
import com.felixtp.storyappsub2.data.remote.response.LoginResponse
import com.felixtp.storyappsub2.data.remote.response.Story
import com.felixtp.storyappsub2.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase,
) {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _message = MutableLiveData<BaseResponse>()
    val message: LiveData<BaseResponse> = _message

    private val _isSuccessLogin = MutableLiveData<Boolean>()
    val isSuccessLogin: LiveData<Boolean> = _isSuccessLogin

    private val _isSuccessRegister = MutableLiveData<Boolean>()
    val isSuccessRegister: LiveData<Boolean> = _isSuccessRegister

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _isUpdated = MutableLiveData<Boolean>()
    val isUpdate: LiveData<Boolean> = _isUpdated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getAllStoresMap = MutableLiveData<List<ListStoryItem>>()
    private val _getDetailStory = MutableLiveData<Story>()

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        _isError.value = false
        _isSuccessRegister.value = false
        _isSuccessLogin.value = false

        apiService.login(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _loginResponse.value = response.body()
                        _isSuccessLogin.value = true
                        _isUpdated.value = true
                    } else {
                        _isError.value = true
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
    }

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        _isError.value = false
        _isSuccessRegister.value = false
        _isSuccessLogin.value = false

        apiService.register(name, email, password)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _message.value = response.body()
                        _isSuccessRegister.value = true
                    } else {
                        _isError.value = true
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
    }

    suspend fun setLogout() {
        userPreference.logout()
    }

    fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?,
    ) {
        _isLoading.value = true
        _isError.value = false

        apiService.addNewStory("Bearer $token", file, description, lat, lon)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.value = response.body()
                            _isUpdated.value = true
                        }
                    } else {
                        _isError.value = true
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
    }

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        _isSuccessLogin.value = false
        _isUpdated.value = false
        _isError.value = false

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryPagingSource(
                storyDatabase, apiService,
                "Bearer $token"
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getDetailStory(token: String, id: String): LiveData<Story> {
        _isLoading.value = true
        _isError.value = false

        apiService.getDetailStory("Bearer $token", id)
            .enqueue(object : Callback<DetailStoryResponse> {
                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _getDetailStory.value = response.body()?.story
                    } else {
                        _isError.value = true
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        return _getDetailStory
    }

    fun getAllStoresMap(token: String, enableLocation: Int): LiveData<List<ListStoryItem>> {
        _isLoading.value = true
        _isError.value = false

        apiService.getAllStoriesMap("Bearer $token", enableLocation)
            .enqueue(object : Callback<GetAllStoriesResponse> {
                override fun onResponse(
                    call: Call<GetAllStoriesResponse>,
                    response: Response<GetAllStoriesResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _getAllStoresMap.value = response.body()?.listStory
                    } else {
                        _isError.value = true
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        return _getAllStoresMap
    }

    fun getSession(): LiveData<Boolean> = userPreference.sessionFlow.asLiveData()
    fun getToken(): LiveData<String> = userPreference.tokenFlow.asLiveData()

    suspend fun saveUser(token: String, isLogin: Boolean) {
        userPreference.saveUser(token, isLogin)
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}

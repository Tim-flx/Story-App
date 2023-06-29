package com.felixtp.storyappsub2.data.remote.retrofit

import com.felixtp.storyappsub2.data.remote.response.BaseResponse
import com.felixtp.storyappsub2.data.remote.response.DetailStoryResponse
import com.felixtp.storyappsub2.data.remote.response.GetAllStoriesResponse
import com.felixtp.storyappsub2.data.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<BaseResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

    @GET("stories")
    fun getAllStoriesMap(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<GetAllStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): Call<BaseResponse>
}

package com.felixtp.storyappsub2.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailStoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val story: Story
)

data class Story(
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("lon")
    val lon: Any,
    @SerializedName("id")
    val id: String,
    @SerializedName("lat")
    val lat: Any
)

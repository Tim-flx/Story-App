package com.felixtp.storyappsub2.data.remote.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GetAllStoriesResponse(
    @SerializedName("listStory")
    val listStory: List<ListStoryItem>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
data class ListStoryItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("lon")
    val lon: Float,
    @SerializedName("lat")
    val lat: Float
)

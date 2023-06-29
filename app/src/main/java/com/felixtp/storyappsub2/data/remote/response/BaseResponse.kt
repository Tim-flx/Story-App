package com.felixtp.storyappsub2.data.remote.response

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)

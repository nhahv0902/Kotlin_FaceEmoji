package com.nhahv.faceemoji.networking.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by nhahv0902 on 10/17/17.
 */
data class BaseResponse<out T>(
        @Expose @SerializedName("data") val data: T,
        @Expose @SerializedName("code") val code: Int,
        @Expose @SerializedName("error") val error: Boolean,
        @Expose @SerializedName("status") val status: Int,
        @Expose @SerializedName("messages") val messages: String
)

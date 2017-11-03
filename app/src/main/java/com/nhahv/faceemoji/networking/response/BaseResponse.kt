package com.nhahv.faceemoji.networking.response

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by nhahv0902 on 10/17/17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BaseResponse<out T : Parcelable>(
        @Expose @SerializedName("data") val data: T,
        @Expose @SerializedName("code") val code: Int,
        @Expose @SerializedName("error") val error: Boolean,
        @Expose @SerializedName("status") val status: Int,
        @Expose @SerializedName("messages") val messages: String
) : Parcelable

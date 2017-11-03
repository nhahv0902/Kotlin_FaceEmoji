package com.nhahv.faceemoji.networking.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by nhahv0902 on 10/17/17.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class ErrorMessage(val message: ArrayList<String>?) : Parcelable {
    fun getMessage(): String {
        message?.let {
            var message = ""
            it.forEach { item ->
                message += ", $item"
            }
            return message
        }
        return ""
    }
}
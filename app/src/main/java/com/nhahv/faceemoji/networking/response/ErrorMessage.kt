package com.nhahv.faceemoji.networking.response

/**
 * Created by nhahv0902 on 10/17/17.
 */
data class ErrorMessage(val message: ArrayList<String>?) {
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
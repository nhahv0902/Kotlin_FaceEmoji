package com.nhahv.faceemoji.networking.error

import com.nhahv.faceemoji.networking.response.BaseResponse
import com.nhahv.faceemoji.networking.response.ErrorMessage
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by nhahv0902 on 10/17/17.
 * Base exception when use Retrofit
 */
class BaseException : RuntimeException {

    val errorType: String
    var errorResponse: BaseResponse<ErrorMessage>? = null
    private lateinit var mResponse: Response<*>

    private constructor(type: String, cause: Throwable) : super(cause.message, cause) {
        errorType = type
    }

    private constructor(type: String, response: Response<*>) {
        errorType = type
        mResponse = response
    }

    constructor(type: String, response: BaseResponse<ErrorMessage>?) {
        errorType = type
        errorResponse = response
    }

    fun getMessgeError(): String {
        return when (errorType) {
            Type.SERVER -> {
                errorResponse?.messages!!
            }
            Type.NETWORK -> {
                getNetworkErrorMessage(cause)
            }
            Type.HTTP -> {
                mResponse.code().getHttpErrorMessage()
            }
            else -> "Error"
        }
    }

    private fun getNetworkErrorMessage(throwable: Throwable?): String {
        if (throwable is SocketTimeoutException) {
            return throwable.message.toString()
        }

        if (throwable is UnknownHostException) {
            return throwable.message.toString()
        }

        if (throwable is IOException) {
            return throwable.message.toString()
        }

        return throwable?.message.toString()
    }

    private fun Int.getHttpErrorMessage(): String {
        if (this in 300..308) {
            // Redirection
            return "It was transferred to a different URL. I'm sorry for causing you trouble"
        }
        if (this in 400..451) {
            // Client error
            return "An error occurred on the application side. Please try again later!"
        }
        if (this in 500..511) {
            // Server error
            return "A server error occurred. Please try again later!"
        }

        // Unofficial error
        return "An error occurred. Please try again later!"
    }

    companion object {
        fun toNetworkError(cause: Throwable): BaseException {
            return BaseException(Type.NETWORK, cause)
        }

        fun toHttpError(response: Response<*>): BaseException {
            return BaseException(Type.HTTP, response)
        }

        fun toUnexpectedError(cause: Throwable): BaseException {
            return BaseException(Type.UNEXPECTED, cause)
        }

        fun toServerError(response: BaseResponse<ErrorMessage>): BaseException {
            return BaseException(Type.SERVER, response)
        }
    }
}
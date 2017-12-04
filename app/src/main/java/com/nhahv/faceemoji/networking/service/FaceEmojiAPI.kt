package com.nhahv.faceemoji.networking.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by nhahv on 10/17/17.
 */
interface FaceEmojiAPI {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("uploadBase64_JSON")
    fun uploadImage(@Field("base64data") base64data: String): Call<String>
}
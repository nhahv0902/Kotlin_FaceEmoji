package com.nhahv.faceemoji.networking.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by nhahv on 10/17/17.
 */
interface FaceEmojiAPI {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("uploadBase64")
    fun uploadImage(@Field("base64data") base64data: String): Call<String>


    @Multipart
    @POST("upload")
    fun upFileImage(@Part("upload") description: RequestBody,
               @Part file: MultipartBody.Part): Call<String>
}
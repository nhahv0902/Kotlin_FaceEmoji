package com.nhahv.faceemoji.networking

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhahv.faceemoji.BuildConfig
import com.nhahv.faceemoji.networking.middleware.InterceptorImpl
import com.nhahv.faceemoji.networking.service.BooleanAdapter
import com.nhahv.faceemoji.networking.service.FaceEmojiAPI
import com.nhahv.faceemoji.networking.service.IntegerAdapter
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by nhahv on 10/17/17.
 * Module for Retrofit dagger2
 */

class NetworkService(private val application: Context) {

    private fun createGson(): Gson {
        val booleanAdapter = BooleanAdapter()
        val integerAdapter = IntegerAdapter()
        return GsonBuilder()
                .registerTypeAdapter(Boolean::class.java, booleanAdapter)
                .registerTypeAdapter(Boolean::class.javaPrimitiveType, booleanAdapter)
                .registerTypeAdapter(Int::class.java, integerAdapter)
                .registerTypeAdapter(Int::class.javaPrimitiveType, integerAdapter)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    private fun createOkHttpCache(): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    private fun createOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.cache(createOkHttpCache())
        httpClientBuilder.addInterceptor(InterceptorImpl())
        httpClientBuilder.readTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        httpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            httpClientBuilder.addInterceptor(logging)
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        return httpClientBuilder.build()
    }

    fun getAPI(): FaceEmojiAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(END_POINT_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(OkHttpClient())
                .build()
        return retrofit.create(FaceEmojiAPI::class.java)
    }

    companion object {
        private val CONNECTION_TIMEOUT = 60
        val END_POINT_URL = "http://139.59.112.147/"

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: NetworkService? = null

        fun getInstance(application: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: NetworkService(application).also { INSTANCE = it }
                }

    }
}
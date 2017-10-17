package com.nhahv.faceemoji.networking

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.nhahv.faceemoji.BuildConfig
import com.nhahv.faceemoji.networking.middleware.InterceptorImpl
import com.nhahv.faceemoji.networking.middleware.RxErrorHandlingCallAdapterFactory
import com.nhahv.faceemoji.networking.service.BooleanAdapter
import com.nhahv.faceemoji.networking.service.FaceEmojiAPI
import com.nhahv.faceemoji.networking.service.IntegerAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by nhahv on 10/17/17.
 * Module for Retrofit dagger2
 */

@Module
class NetworkModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application = application

    @Singleton
    @Provides
    fun provideGson(): Gson {
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

    @Singleton
    @Provides
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Singleton
    @Provides
    fun provideInterceptorImpl(): Interceptor = InterceptorImpl()

    @Singleton
    @Provides
    fun provideOkHttpClient(cache: Cache, interceptor: Interceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder();
        httpClientBuilder.cache(cache)
        httpClientBuilder.addInterceptor(interceptor)
        httpClientBuilder.readTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        httpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            httpClientBuilder.addInterceptor(logging)
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        return httpClientBuilder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(END_POINT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

    @Singleton
    @Provides
    fun provideAPI(retrofit: Retrofit): FaceEmojiAPI = retrofit.create(FaceEmojiAPI::class.java)

    companion object {
        private val CONNECTION_TIMEOUT = 60
        val END_POINT_URL = ""
    }
}
package com.nhahv.faceemoji.networking.middleware

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

/**
 * Created by nhahv0902 on 10/17/17.
 * Add Header for Retrofit
 */
class InterceptorImpl : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = initializeHeader(chain)
        var request = builder.build()
        var response = chain.proceed(request)
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//            refreshToken();
//            builder.addHeader("Authorization", "Bearer " + accessToken);
            request = builder.build()
            response = chain.proceed(request)
        }
        return response
    }


    private fun initializeHeader(chain: Interceptor.Chain): Request.Builder {
        val originRequest = chain.request()
        return originRequest.newBuilder()
                .header("Accept", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cache-Control", "no-store")
//                .header("Authorization", "Bearer " + accessToken)
                .method(originRequest.method(), originRequest.body())
    }
}
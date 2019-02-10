package com.arctouch.codechallenge.data.api

import com.arctouch.codechallenge.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import okhttp3.HttpUrl



/**
 * Created by diefferson.santos
 */
class RestClient() {

    val api: TmdbApi

    init {

        val httpClientBuilder = OkHttpClient.Builder().addInterceptor(this::apiParamsInterceptor)

        if (BuildConfig.HTTP_LOG_ENABLED) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        val httpClient = httpClientBuilder.build()

        val retrofitClient: Retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        api = retrofitClient.create(TmdbApi::class.java)
    }

    @Throws(IOException::class)
    private fun apiParamsInterceptor(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url()
                .newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .addQueryParameter("language", BuildConfig.DEFAULT_LANGUAGE)
                .build()
        request = request.newBuilder().url(url).build()

        return chain.proceed(request)
    }
}

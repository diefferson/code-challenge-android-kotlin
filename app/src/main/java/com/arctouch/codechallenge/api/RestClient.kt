package com.arctouch.codechallenge.api

import com.arctouch.codechallenge.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by diefferson.santos
 */
class RestClient() {

    val api: TmdbApi

    init {

        val httpClientBuilder = OkHttpClient.Builder()

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
}

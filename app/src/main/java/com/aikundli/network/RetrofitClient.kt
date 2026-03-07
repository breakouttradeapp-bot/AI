package com.aikundli.network

import com.aikundli.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ── Backend Retrofit ──────────────────────────────────────────────────

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val backendRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val kundliApi: KundliApiService by lazy {
        backendRetrofit.create(KundliApiService::class.java)
    }

    // ── Cerebras Retrofit ──────────────────────────────────────────────────

    private val cerebrasRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cerebras.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cerebrasApi: CerebrasApiService by lazy {
        cerebrasRetrofit.create(CerebrasApiService::class.java)
    }
}

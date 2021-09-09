package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.inet

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object InetApiClient {
    private var BASE_URL: String = "http://checkip.amazonaws.com"

    val httpClient : OkHttpClient by lazy  {
        OkHttpClient.Builder().callTimeout(15, TimeUnit.SECONDS).connectTimeout(15, TimeUnit
            .SECONDS).readTimeout(15, TimeUnit.SECONDS).connectionPool(
            ConnectionPool(15,150,
                TimeUnit.SECONDS)
        ).build()
    }

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val apiService : InetApiService by lazy {
        retrofit.create(InetApiService::class.java)
    }
}
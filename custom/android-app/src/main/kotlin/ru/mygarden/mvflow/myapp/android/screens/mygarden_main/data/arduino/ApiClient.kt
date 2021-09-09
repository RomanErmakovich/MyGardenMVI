package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.CommonFun
import java.util.concurrent.TimeUnit

object ApiClient {
    //private const val BASE_URL: String = "http://192.168.1.163:80/"
    private var BASE_URL: String = "http://"+ CommonFun.hostIP +"/"

    private val gson : Gson by lazy {
        GsonBuilder().setLenient().create()
    }

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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.inet

import retrofit2.http.GET
import ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino.ArdBean

interface InetApiService {

    @GET("http://checkip.amazonaws.com")
    suspend fun getRemoteIP(): String?
}
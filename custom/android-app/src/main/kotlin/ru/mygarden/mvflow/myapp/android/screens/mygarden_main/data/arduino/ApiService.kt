package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.arduino

import retrofit2.http.GET

interface ApiService {

    @GET("d")
    suspend fun getAllInfo(): List<ArdBean>

    @GET("0")
    suspend fun closeAllGr(): ArdBean

    @GET("1")
    suspend fun open1Gr(): ArdBean

    @GET("2")
    suspend fun open12Gr(): ArdBean

    @GET("3")
    suspend fun open123Gr(): ArdBean

    @GET("k")
    suspend fun water1(): ArdBean

    @GET("l")
    suspend fun water0(): ArdBean

    @GET("w")
    suspend fun heat1(): ArdBean

    @GET("x")
    suspend fun heat0(): ArdBean


}

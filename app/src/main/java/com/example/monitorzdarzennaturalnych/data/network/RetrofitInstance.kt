package com.example.monitorzdarzennaturalnych.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://eonet.gsfc.nasa.gov/api/v3/"

    // Inicjalizacja połączenia HTTP (tzw. "leniwa inicjalizacja", wykonuje się tylko raz po wywołaniu)
    val api: EonetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Przekonwertowanie tekstu/JSON na obiekty Kotlina
            .build()
            .create(EonetApiService::class.java)
    }
}

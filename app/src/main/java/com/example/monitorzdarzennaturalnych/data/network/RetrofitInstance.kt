package com.example.monitorzdarzennaturalnych.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// glowne narzedzie do polaczen sieciowych
object RetrofitInstance {
    // bazowy link do nasa eonet
    private const val BASE_URL = "https://eonet.gsfc.nasa.gov/api/v3/"

    // "leniwe" wywolanie API
    val api: EonetApiService by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // tlumaczenie z json
                .build()
                .create(EonetApiService::class.java)
    }
}

package com.example.monitorzdarzennaturalnych.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Zapewnia jedno globalne, współdzielone wystąpienie klienta HTTP w całej aplikacji. Zapobiega
 * nadmiernemu zużyciu zasobów (takich jak pamięć RAM).
 */
object RetrofitInstance {
    // Bazowy adres URL NASA EONET API w wersji 3.
    private const val BASE_URL = "https://eonet.gsfc.nasa.gov/api/v3/"

    // Konfiguracja klienta HTTP z dłuższym czasem oczekiwania (NASA API bywa powolne)
    private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    // Leniwa inicjalizacja (`by lazy`) usługi API. Obiekt klasy EonetApiService zostanie
    // utworzony dopiero wtedy, gdy po raz pierwszy odwołamy się do właściwości `api`. Kolejne
    // odwołania zwrócą już wcześniej utworzoną instancję.
    val api: EonetApiService by lazy {
        Retrofit.Builder()
                // Ustawienie głównego adresu URL serwera
                .baseUrl(BASE_URL)
                .client(client)
                // Dodanie konwertera GSON do automatycznej serializacji i deserializacji obiektów
                // JSON na klasy Kotlina
                .addConverterFactory(GsonConverterFactory.create())
                // Zbudowanie klienta Retrofit z powyższą konfiguracją
                .build()
                // Wygenerowanie implementacji interfejsu EonetApiService
                .create(EonetApiService::class.java)
    }
}

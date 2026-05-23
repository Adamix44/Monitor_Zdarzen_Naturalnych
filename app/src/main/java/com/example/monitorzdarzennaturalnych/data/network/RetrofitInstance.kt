package com.example.monitorzdarzennaturalnych.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Klient sieciowy Retrofit implementujący wzorzec Singleton (za pomocą słowa kluczowego `object` w Kotlinie).
 * Zapewnia jedno globalne, współdzielone wystąpienie klienta HTTP w całej aplikacji,
 * zapobiegając nadmiernemu zużyciu zasobów (takich jak pamięć RAM).
 */
object RetrofitInstance {
    // Bazowy adres URL NASA EONET API w wersji 3.
    private const val BASE_URL = "https://eonet.gsfc.nasa.gov/api/v3/"
    
    //Leniwa inicjalizacja (`by lazy`) usługi API.
    //Obiekt klasy EonetApiService zostanie utworzony dopiero wtedy, gdy po raz pierwszy
    //odwołamy się do właściwości `api`. Kolejne odwołania zwrócą już wcześniej utworzoną instancję.
    val api: EonetApiService by lazy {
        Retrofit.Builder()
                //Ustawienie głównego adresu URL serwera
                .baseUrl(BASE_URL)
                //Dodanie konwertera GSON do automatycznej serializacji i deserializacji obiektów JSON na klasy Kotlina
                .addConverterFactory(GsonConverterFactory.create())
                //Zbudowanie klienta Retrofit z powyższą konfiguracją
                .build()
                //Wygenerowanie implementacji interfejsu EonetApiService 
                .create(EonetApiService::class.java)
    }
}


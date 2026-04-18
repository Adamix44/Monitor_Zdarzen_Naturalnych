package com.example.monitorzdarzennaturalnych.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Obiekt (Singleton w Kotlinie) zarządzający połączeniem (tworzony jest tylko jeden na całą żywotność włączonej Aplikacji).
 * Mówi: Do jasnej cholery nie łączmy się od zera i pożerajmy RAM przy każdej próbie załadowania mapy, zróbmy to rygorystycznie raz.
 */
object RetrofitInstance {
    // Tu zaszyliśmy ścieżkę stałą w formie String (stała w całym oprogramowaniu - z tego pnia zaczynamy budować dodawanie "/events")
    private const val BASE_URL = "https://eonet.gsfc.nasa.gov/api/v3/"

    /**
     * Konfigurujemy sam interfejs używając by "lazy". 
     * Mówi on to oznaczające: ta wirtualna zmienna wykona zawartość w swoich klamrach DOPIERO i tylko wyłącznie, gdy wywołasz mnie używając mnie pierwszy raz za prośbą.
     * Zapobiegnie zawieszaniu złącza po rozruchu, przyspieszając szybkość. Używa Gson Converter do tłumaczenia płaskich JSON-owych plików NASA na nasze "Data Class".
     */
    val api: EonetApiService by lazy {
        Retrofit.Builder()
            // Podpinamy bazowy fragment HTTP pod narzędzie komunikacji wejściowe.
            .baseUrl(BASE_URL)
            // Określamy bibliotece jak ma rozpakowywać zagraniczne wiadomości.
            .addConverterFactory(GsonConverterFactory.create()) // Przekonwertowanie tekstu/JSON na obiekty Kotlina.
            .build()
            // Odnosimy plik z instrukcją wyciągu "/events" do budowy narzędzia i podłączamy obwód do interfejsu EonetApiService.kt.
            .create(EonetApiService::class.java)
    }
}

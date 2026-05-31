package com.example.monitorzdarzennaturalnych.data.network

import com.example.monitorzdarzennaturalnych.BuildConfig
import com.example.monitorzdarzennaturalnych.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfejs API definiujący punkty końcowe (endpoints) komunikacji sieciowej z NASA EONET. Retrofit
 * generuje implementację tego interfejsu automatycznie w czasie wykonywania aplikacji.
 */
interface EonetApiService {
    /**
     * Pobiera listę zdarzeń naturalnych z NASA EONET API. Wywołanie wykonuje zapytanie typu HTTP
     * GET. Funkcja została oznaczona słowem kluczowym `suspend`, co oznacza, że jest to funkcja
     * zawieszalna. Umożliwia to asynchroniczne pobieranie danych przy użyciu Kotlin Coroutines bez
     * blokowania głównego wątku UI.
     *
     * @param status Status zdarzeń. Wartość "all" pozwala na pobranie zarówno zdarzeń aktywnych
     * (open), jak i zakończonych (closed).
     * @param days Zakres czasu w dniach wstecz.
     * @param apiKey Klucz API NASA.
     * @return Zwraca deserializowany obiekt odpowiedzi [EventResponse] zawierający listę zdarzeń.
     */
    @GET("events")
    suspend fun getEvents(
            @Query("status") status: String = "all",
            @Query("days") days: Int = 20,
            @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): EventResponse
}

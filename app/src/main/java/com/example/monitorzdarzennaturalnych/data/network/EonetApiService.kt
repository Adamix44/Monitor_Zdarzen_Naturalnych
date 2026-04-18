package com.example.monitorzdarzennaturalnych.data.network

import com.example.monitorzdarzennaturalnych.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * EonetApiService to nasz interfejs stanowiący instrukcję dla modułu internetowego.
 * Używamy biblioteki Retrofit, więc zamiast samemu pisać 20 linijek konfiguracji pobierania tekstu i zamieniania streamu na dane,
 * my tylko "przyczepiamy" znaczniki a Retrofit domyśla się, pod jaki link zaatakować.
 */
interface EonetApiService {
    
    // Adnotacja @GET("events") dodaje "/events" do adresu głównego. Cały link ubiega więc do "nasa...api/v3/events".
    @GET("events")
    // Pobieramy zdarzenia w sposób pasywny "suspend" podając opcjonalnie by z Defaulta ściągać tylko te aktywne "open" zdarzenia na globie.
    suspend fun getEvents(
        @Query("status") status: String = "open"
    ): EventResponse
}

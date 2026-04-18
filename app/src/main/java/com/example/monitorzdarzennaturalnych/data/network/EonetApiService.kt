package com.example.monitorzdarzennaturalnych.data.network

import com.example.monitorzdarzennaturalnych.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EonetApiService {
    
    // Wskazujemy ścieżkę API NASA, pobieramy otwarte/aktywne wydarzenia
    @GET("events")
    suspend fun getEvents(
        @Query("status") status: String = "open"
    ): EventResponse
}

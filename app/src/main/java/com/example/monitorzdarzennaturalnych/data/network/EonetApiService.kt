package com.example.monitorzdarzennaturalnych.data.network

import com.example.monitorzdarzennaturalnych.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

// interfejs sieciowy do pobierania danych NASA
interface EonetApiService {
    
    // cel API dla aktywnych wydarzen
    @GET("events")
    suspend fun getEvents(
        @Query("status") status: String = "open"
    ): EventResponse
}

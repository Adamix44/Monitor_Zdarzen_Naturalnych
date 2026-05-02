package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// posrednik umozliwiajacy bezpieczne laczenie z NASA
class EventRepository {

    // asynchroniczna funkcja dzialajaca w tle
    suspend fun getEvents(): List<Event> {
        // wydelegowanie do narzedzi zapis/odczyt (IO) by nie zatrzymac ekranu
        return withContext(Dispatchers.IO) {
            try {
                // proba sciagniecia listy z wygenerowanego zlacza Retrofit
                val response = RetrofitInstance.api.getEvents()
                response.events
            } catch (e: Exception) {
                // w razie braku sieci, przekazujemy pusta liste i unikamy bledu
                emptyList()
            }
        }
    }
}

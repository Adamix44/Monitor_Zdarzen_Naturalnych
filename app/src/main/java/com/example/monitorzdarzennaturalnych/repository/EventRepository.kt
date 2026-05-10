package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// umozliwia bezpieczne laczenie z NASA
class EventRepository {
    // funkcja dzialajaca w tle
    suspend fun getEvents(): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getEvents()
                response.events
            } catch (e: Exception) {
                // w razie braku sieci, przekazujemy pusta liste i unikamy bledu
                emptyList()
            }
        }
    }
}

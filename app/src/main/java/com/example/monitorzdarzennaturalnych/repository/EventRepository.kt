package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// umozliwia bezpieczne laczenie z NASA
class EventRepository {
    // funkcja dzialajaca w tle
    suspend fun getEvents(days: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getEvents(days = days)
                response.events
            } catch (e: Exception) {
                // Wydrukuj bląd do logcat, żebyśmy wiedzieli co ubiło parsowanie
                e.printStackTrace()
                emptyList()
            }
        }
    }
}

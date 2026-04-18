package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository {
    // Funkcja 'suspend' działa asynchronicznie, dzięki temu pobieranie z sieci nie "zawiesza" aplikacji
    suspend fun getEvents(): List<Event> {
        return withContext(Dispatchers.IO) { // Dispatchers.IO określa, że używamy strumienia zoptymalizowanego pod sieć/zapis
            try {
                // Wywołujemy zdefiniowaną wczesniej fukncję z Retrofit, pobiera listę naturalnych wydarzeń wprost od NASA
                val response = RetrofitInstance.api.getEvents()
                response.events
            } catch (e: Exception) {
                // W razie braku połączenia internetowego lub błędu serwera zwracamy pustą listę zamiast "wywalać" aplikację
                emptyList()
            }
        }
    }
}

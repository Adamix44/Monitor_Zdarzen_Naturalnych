package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository (repozytorium) stanowiące warstwę abstrakcji nad źródłami danych.
 * Izoluje logikę aplikacji oraz ViewModel od szczegółów pobierania danych.
 * W tej architekturze repozytorium pobiera dane bezpośrednio z NASA EONET API przy użyciu [RetrofitInstance].
 */
class EventRepository {
    /**
     * Pobiera listę wydarzeń z serwera NASA.
     * Użycie `withContext(Dispatchers.IO)` przełącza kontekst wykonania tej korutyny na wątek z puli I/O.
     * Zapobiega to zablokowaniu głównego wątku aplikacji (UI Thread),dzięki czemu interfejs użytkownika pozostaje w pełni płynny.
     * @param days Zakres czasu w dniach wstecz, dla którego mają być pobrane zdarzenia.
     * @return Lista obiektów [Event] reprezentujących pobrane zdarzenia. Zwraca pustą listę w przypadku błędu.
     */
    suspend fun getEvents(days: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                //wykonanie synchronicznego wewnątrz wątku I/O zapytania sieciowego
                val response = RetrofitInstance.api.getEvents(days = days)
                
                //zwrócenie wyodrębnionej listy zdarzeń
                response.events
            } catch (e: Exception) {
                // Wypisanie szczegółowego śladu błędu do Logcat w celach diagnostycznych
                e.printStackTrace()
                
                //Zapewnienie stabilności aplikacji: w przypadku braku połączenia internetowego, limitu żądań
                //lub błędnego parsowania, funkcja nie rzuca wyjątku wyżej (co mogłoby wywołać crash),
                //lecz bezpiecznie zwraca pustą listę zdarzeń.
                emptyList()
            }
        }
    }
}


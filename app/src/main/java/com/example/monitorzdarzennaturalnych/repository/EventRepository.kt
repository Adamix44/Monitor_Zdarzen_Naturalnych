package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository (repozytorium) izoluje logikę aplikacji oraz ViewModel od szczegółów pobierania
 * danych. W tej architekturze repozytorium pobiera dane bezpośrednio z NASA EONET API przy użyciu
 * [RetrofitInstance].
 */
class EventRepository {
    /**
     * Pobiera listę wydarzeń z serwera NASA. Użycie `withContext(Dispatchers.IO)` przełącza
     * kontekst wykonania tej korutyny na wątek z puli I/O. Zapobiega to zablokowaniu głównego wątku
     * aplikacji (UI Thread),dzięki czemu interfejs użytkownika pozostaje w pełni płynny.
     * @param days Zakres czasu w dniach wstecz, dla którego mają być pobrane zdarzenia.
     * @return Lista obiektów reprezentujących zdarzenia. Zwraca pustą listę w przypadku błędu.
     */
    suspend fun getEvents(days: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            try {
                // wykonanie zapytania sieciowego
                val response = RetrofitInstance.api.getEvents(days = days)

                // zwrócenie listy zdarzeń
                response.events
            } catch (e: Exception) {
                // Wypisanie śladu błędu
                e.printStackTrace()
                // zwraca pustą listę zdarzeń.
                emptyList()
            }
        }
    }
}

package com.example.monitorzdarzennaturalnych.repository

import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repozytorium ma zazwyczaj jedno zadanie: pośredniczyć między aplikacją (np. ekranem a siecią/bazą danych).
 * Dzięki niemu w Activity nie musimy martwić się o wyciąganie błędów, robimy tam wyłącznie wizualizację.
 * To centralne miejsce łączące dane NASA z resztą systemu na platformie telefonu.
 */
class EventRepository {
    
    /**
     * Funkcja 'suspend' z Coroutines - działa i "usypia się" w tle.
     * Zwykłe zapytanie HTTP mogłoby trwać nawet 5 sekund zacinając całkowicie ekran telefonu.
     * Użycie suspend sprawia, że telefon wciąż odpowiada na animacje użytkownika obok.
     * 
     * Zwraca nam ona List<Event> czyli Listę wszystkich naturalnych zdarzeń z serwerów państwowych.
     */
    suspend fun getEvents(): List<Event> {
        // withContext pozwala bezpiecznie wypchnąć to na oddzielne wątki sprzętowe (IO to procesory przygotowane m.in. dla operacji zapis/odczyt internetu).
        return withContext(Dispatchers.IO) { 
            try {
                // Składamy prośbę dla wcześniej zadeklarowanego łącza NASA pobierającego wydarzenia
                val response = RetrofitInstance.api.getEvents()
                // Do warstwy powyżej oddajemy od razu samą gotową listę 
                response.events
            } catch (e: Exception) {
                // Try-catch broni nas przed zatrzymanym awarią programem. Rozpoznaje np. brak internetu u użytownika i oddaje puste pole z domyślnego emptyList.
                emptyList()
            }
        }
    }
}

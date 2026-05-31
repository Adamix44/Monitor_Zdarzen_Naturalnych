package com.example.monitorzdarzennaturalnych.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.monitorzdarzennaturalnych.data.AlarmPreferences
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.repository.EventRepository
import com.example.monitorzdarzennaturalnych.worker.EventAlarmWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Tłumaczy oficjalne, angielskie nazwy kategorii zdarzeń zwracane przez NASA EONET API
 * na nazwy w języku polskim.
 * Funkcja używa wyrażenia `when` działającego jako instrukcja warunkowa dopasowania wzorca.
 * Jeśli kategoria nie pasuje do żadnego ze zdefiniowanych kluczy, zwracana jest surowa nazwa angielska.
 *
 * @param title Angielska nazwa kategorii (np. "Wildfires").
 * @return Przetłumaczona nazwa kategorii w języku polskim (np. "Pożary lasów").
 */
fun translateCategory(title: String): String {
    return when(title) {
        "Wildfires" -> "Pożary lasów"
        "Volcanoes" -> "Wulkany"
        "Severe Storms" -> "Gwałtowne burze"
        "Sea and Lake Ice" -> "Lód na morzach i jeziorach"
        "Floods" -> "Powodzie"
        "Earthquakes" -> "Trzęsienia ziemi"
        "Drought" -> "Susze"
        "Dust and Haze" -> "Zadymienie i mgły"
        "Manmade" -> "Stworzone przez człowieka"
        "Snow" -> "Śnieżyce"
        "Temperature Extremes" -> "Ekstremalne temperatury"
        "Water Color" -> "Zabarwienia wody"
        "Landslides" -> "Osuwiska"
        else -> title
    }
}

/**
 * Główna klasa ViewModel w architekturze MVVM (Model-View-ViewModel) dla głównego ekranu aplikacji.
 * Odpowiada za:
 * 1.Przechowywanie i zarządzanie stanem interfejsu użytkownika w sposób odporny na zmiany konfiguracji (np. obrót ekranu).
 * 2.Pośredniczenie między warstwą danych ([EventRepository]) a widokiem (Jetpack Compose).
 * 3.Obsługę logiki, takiej jak filtrowanie danych 
 *
 * Stosuje zasadę enkapsulacji. Właściwości modyfikowalne (_allEvents, _isLoading itp.) są prywatnymi obiektami
 * typu [MutableLiveData], natomiast widok ma dostęp wyłącznie do ich niemodyfikowalnych, publicznych odpowiedników typu [LiveData].
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    /**repozytorium służąca do komunikacji z API NASA.*/
    private val repository = EventRepository()
    private val alarmPreferences = AlarmPreferences(application)

    /**kontener/bufor przechowujący nieprzefiltrowaną listę wszystkich pobranych z NASA zdarzeń.*/
    private val _allEvents = MutableLiveData<List<Event>>()

    /** Stan ładowania danych (true - trwa pobieranie, false - bezczynność).*/
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**Aktualnie zaznaczona przez użytkownika kategoria filtrowania (domyślnie "Wszystkie").*/
    private val _selectedCategory = MutableLiveData<String>("Wszystkie")
    val selectedCategory: LiveData<String> get() = _selectedCategory

    /**Wybrany zakres czasu w dniach do filtrowania danych (domyślnie 30 dni).*/
    private val _selectedDays = MutableLiveData<Int>(30)
    val selectedDays: LiveData<Int> get() = _selectedDays

    /**lista kategorii zdarzeń dostępnych w pobranych aktualnie danych NASA.*/
    private val _availableCategories = MutableLiveData<List<String>>(listOf("Wszystkie"))
    val availableCategories: LiveData<List<String>> get() = _availableCategories

    /**Przefiltrowana lista zdarzeń, która jest renderowana na mapie*/
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    /**Zdarzenie wybrane/kliknięte przez użytkownika (służy do otwierania dolnego panelu szczegółów)*/
    private val _selectedEvent = MutableLiveData<Event?>()
    val selectedEvent: LiveData<Event?> get() = _selectedEvent

    /**(true - widok listy zdarzeń, false - widok interaktywnej mapy).*/
    private val _isListView = MutableLiveData<Boolean>(false)
    val isListView: LiveData<Boolean> get() = _isListView

    // --- STAN ALARMU ---
    private val _alarmEnabled = MutableLiveData<Boolean>(alarmPreferences.alarmEnabled)
    val alarmEnabled: LiveData<Boolean> get() = _alarmEnabled

    private val _alarmRadiusKm = MutableLiveData<Int>(alarmPreferences.radiusKm)
    val alarmRadiusKm: LiveData<Int> get() = _alarmRadiusKm


    //funkcje zmiany stanu/intencje uzytkownika
    /**
     * Zmienia tryb wyświetlania między mapą a listą.
     * @param isList True dla widoku listy, false dla widoku mapy.
     */
    fun setListView(isList: Boolean)
    {
        _isListView.value = isList
    }

    /**
     * Wybiera konkretne zdarzenie w celu wyświetlenia jego szczegółów na dolnej karcie.
     * @param event Obiekt zdarzenia 
     */
    fun selectEvent(event: Event?) 
    {
        _selectedEvent.value = event
    }

    /**
     * uruchamia filtrowanie
     * @param category Nazwa kategorii w języku polskim.
     */
    fun setCategory(category: String) 
    {
        _selectedCategory.value = category
        applyFilters()
    }

    /**
     * Zmienia zakres dni
     * Ta zmiana wymaga pobrania nowych danych z NASA, dlatego wywoływana jest metoda [loadEvents].
     * @param days Liczba dni wstecz 
     */
    fun setDays(days: Int) 
    {
        _selectedDays.value = days
        loadEvents()
    }

    // --- FUNKCJE ALARMU ---

    /**
     * Włącza alarm z podanym zasięgiem w kilometrach.
     * radiusKm = 0 oznacza monitorowanie całej planety.
     */
    fun enableAlarm(radiusKm: Int) {
        alarmPreferences.alarmEnabled = true
        alarmPreferences.radiusKm = radiusKm

        // Zapamiętaj aktualnie znane zdarzenia, żeby nie powiadamiać o już istniejących
        val currentIds = _allEvents.value?.map { it.id }?.toSet() ?: emptySet()
        alarmPreferences.lastCheckedEventIds = currentIds

        _alarmEnabled.value = true
        _alarmRadiusKm.value = radiusKm

        scheduleAlarmWorker()
    }

    /** Wyłącza alarm i zatrzymuje periodyczne sprawdzanie */
    fun disableAlarm() {
        alarmPreferences.clearAlarm()
        _alarmEnabled.value = false
        _alarmRadiusKm.value = 0

        WorkManager.getInstance(getApplication<Application>())
            .cancelUniqueWork(EventAlarmWorker.WORK_NAME)
    }

    /** Planuje periodyczne sprawdzanie zdarzeń co 15 minut */
    private fun scheduleAlarmWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<EventAlarmWorker>(
            15, TimeUnit.MINUTES // minimalne okno dla WorkManager
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication<Application>())
            .enqueueUniquePeriodicWork(
                EventAlarmWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    //funkcje sieciowe
    /**
     * pobieranie danych z NASA EONET API.
     * Jeśli użytkownik opuści ekran i ViewModel zostanie zniszczony, wszelkie trwające żądania sieciowe
     * w tym zakresie zostaną automatycznie anulowane.
     */
    fun loadEvents()
    {
        viewModelScope.launch {
            //wskaźnik ładowania w UI
            _isLoading.value = true
            
            //pobranie aktualnie wybranego zakresu dni
            val days = _selectedDays.value ?: 30
            
            //pobranie danych z repozytorium (z mechanizmem ponawiania)
            var result = repository.getEvents(days)

            // Jeśli API NASA nie odpowiedziało (pusta lista), spróbuj jeszcze raz po krótkiej chwili
            if (result.isEmpty()) {
                kotlinx.coroutines.delay(1500)
                result = repository.getEvents(days)
            }
            
            //zapisanie pełnej listy
            _allEvents.value = result
            
            //flatMap - zmiana list kategorii ze wszystkich zdarzeń do jednej listy.
            //map - przetłumaczenie nazw kategorii na język polski.
            //distinct - usunięcie duplikatów.
            //sorted - alfabetyczne posortowanie.
            val cats = result.flatMap { it.categories }
                             .map { translateCategory(it.title) }
                             .distinct()
                             .sorted()
            
            //lista kategorii z domyślną opcją "Wszystkie" na początku
            _availableCategories.value = listOf("Wszystkie") + cats

            //zastosowanie filtrów na pobranych danych
            applyFilters()
            
            //wyłączenie wskaźnika ładowania w UI
            _isLoading.value = false
        }
    }

    /**
     * filtruje pełną listę zdarzeń przechowywaną w buforze [_allEvents] 
     * na podstawie wybranej kategorii [_selectedCategory] i publikuje wynik do [_events].
     */
    private fun applyFilters()
    {
        val all = _allEvents.value ?: emptyList()
        val cat = _selectedCategory.value ?: "Wszystkie"
        
        val filtered = if (cat == "Wszystkie") {
            //jeśli "Wszystkie" -> przekazujemy kompletną listę
            all
        } 
        else 
        {
            //sprawdzamy czy zdarzenie ma kategorię o przetłumaczonej nazwie równej 'cat'
            all.filter { event -> 
                event.categories.any { translateCategory(it.title) == cat }
            }
        }
        //aktualizacja LiveData 
        _events.value = filtered
    }
}


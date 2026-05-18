package com.example.monitorzdarzennaturalnych.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.repository.EventRepository
import kotlinx.coroutines.launch

// oddziela logike od ekranu
class MainViewModel : ViewModel() {
    private val repository = EventRepository()

    // Wszystkie pobrane dane (nieprzefiltrowane)
    private val _allEvents = MutableLiveData<List<Event>>()

    // Stan Ladowania
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Filtry: Kategoria
    private val _selectedCategory = MutableLiveData<String>("Wszystkie")
    val selectedCategory: LiveData<String> get() = _selectedCategory

    // Filtry: Czas (Opcja A)
    private val _selectedDays = MutableLiveData<Int>(30) // domyslnie ostatni miesiac
    val selectedDays: LiveData<Int> get() = _selectedDays

    // Dostepne Kategorie z NASA
    private val _availableCategories = MutableLiveData<List<String>>(listOf("Wszystkie"))
    val availableCategories: LiveData<List<String>> get() = _availableCategories

    // Ostateczna, przefiltrowana lista dla Mapy/Listy
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    // Wybrane zjawisko (do pokazania dolnej karty)
    private val _selectedEvent = MutableLiveData<Event?>()
    val selectedEvent: LiveData<Event?> get() = _selectedEvent

    // Tryb Wyswietlania: Mapa vs Lista (Opcja C)
    private val _isListView = MutableLiveData<Boolean>(false)
    val isListView: LiveData<Boolean> get() = _isListView


    // --- FUNKCJE ZMIANY STANU ---

    fun setListView(isList: Boolean) {
        _isListView.value = isList
    }

    fun selectEvent(event: Event?) {
        _selectedEvent.value = event
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        applyFilters() // filtrowanie offline (tylko ukrywa pinezki)
    }

    fun setDays(days: Int) {
        _selectedDays.value = days
        loadEvents() // zmiana czasu wymaga pobrania nowych danych z NASA
    }

    // --- FUNKCJE SIECIOWE ---

    // funkcja wymuszajaca odswiezenie
    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // pobranie danych na podstawie wybranego czasu
            val days = _selectedDays.value ?: 30
            val result = repository.getEvents(days)
            _allEvents.value = result
            
            // Wydobycie unikalnych nazw kategorii z pobranych zjawisk
            val cats = result.flatMap { it.categories }.map { it.title }.distinct().sorted()
            _availableCategories.value = listOf("Wszystkie") + cats

            applyFilters()
            _isLoading.value = false
        }
    }

    // Aplikuje wybrane filtry na pobrane juz dane
    private fun applyFilters() {
        val all = _allEvents.value ?: emptyList()
        val cat = _selectedCategory.value ?: "Wszystkie"
        
        val filtered = if (cat == "Wszystkie") {
            all
        } else {
            all.filter { event -> 
                event.categories.any { it.title == cat }
            }
        }
        _events.value = filtered
    }
}

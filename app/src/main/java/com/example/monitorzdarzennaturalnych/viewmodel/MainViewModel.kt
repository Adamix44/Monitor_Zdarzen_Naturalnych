package com.example.monitorzdarzennaturalnych.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.repository.EventRepository
import kotlinx.coroutines.launch

// menedzer widoku oddzielajacy logike od ekranu
class MainViewModel : ViewModel() {

    private val repository = EventRepository()

    // ukryty edytowalny strumien danych
    private val _events = MutableLiveData<List<Event>>()
    
    // publiczny strumien tylko do odczytu dla MainActivity
    val events: LiveData<List<Event>> get() = _events

    // funkcja wymuszajaca odswiezenie (zamiast powielac na ekranie)
    fun loadEvents() {
        viewModelScope.launch {
            // prosi repozytorium o pobranie danych i podaje go do strumienia
            val result = repository.getEvents()
            _events.postValue(result)
        }
    }
}

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
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>>
        get() = _events

    // funkcja wymuszajaca odswiezenie
    fun loadEvents() {
        viewModelScope.launch {
            val result = repository.getEvents()
            _events.postValue(result)
        }
    }
}

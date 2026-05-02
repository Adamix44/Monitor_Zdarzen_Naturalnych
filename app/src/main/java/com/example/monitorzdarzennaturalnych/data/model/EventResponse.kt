package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.annotations.SerializedName

// Główna "paczka" danych odbierana od NASA, która otula całą listę zjawisk na świecie.
data class EventResponse(
    @SerializedName("title") val title: String, 
    @SerializedName("events") val events: List<Event> 
)

// Pojedyncze wydarzenie naturalne, np. konkretny pożar lasu w Australii czy sztorm.
data class Event(
    @SerializedName("id") val id: String, 
    @SerializedName("title") val title: String, 
    @SerializedName("categories") val categories: List<Category>, 
    @SerializedName("geometry") val geometries: List<Geometry> 
)

// Kategoria wydarzenia, pomaga przypisać czy dany wpis to powódź czy wulkan.
data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
)

// Geometria przechowująca dokładny czas wykrycia oraz współrzędne na mapę.
data class Geometry(
    @SerializedName("date") val date: String, 
    @SerializedName("type") val type: String, 
    @SerializedName("coordinates") val coordinates: List<Double> // Zawsze zapisane jako [długość, szerokość] geograficzna
)

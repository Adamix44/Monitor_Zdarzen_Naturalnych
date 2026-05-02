package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.annotations.SerializedName

// glowne dane odbierane od NASA
data class EventResponse(
        @SerializedName("title") val title: String,
        @SerializedName("events") val events: List<Event>
)

// pojedyncze wydarzenie naturalne
data class Event(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("categories") val categories: List<Category>,
        @SerializedName("geometry") val geometries: List<Geometry>
)

// kategoria pojedynczego wydarzenia naturalnego
data class Category(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String
)

// dokladny czas wykrycia oraz wspolrzedne na mape
data class Geometry(
        @SerializedName("date") val date: String,
        @SerializedName("type") val type: String,
        @SerializedName("coordinates")
        val coordinates: List<Double> // zapis [długość, szerokość] geograficzna
)

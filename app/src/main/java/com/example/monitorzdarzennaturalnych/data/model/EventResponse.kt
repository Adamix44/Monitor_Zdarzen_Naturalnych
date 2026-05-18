package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonArray

// glowne dane odbierane od NASA
data class EventResponse(
        @SerializedName("title") val title: String,
        @SerializedName("events") val events: List<Event>
)

// pojedyncze wydarzenie
data class Event(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("categories") val categories: List<Category>,
        @SerializedName("geometry") val geometries: List<Geometry>
)

// kategoria pojedynczego wydarzenia
data class Category(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String
)

// dokladny czas wykrycia oraz wspolrzedne geograficzne
data class Geometry(
        @SerializedName("date") val date: String,
        @SerializedName("type") val type: String,
        @SerializedName("coordinates")
        val coordinates: JsonArray // uzywamy JsonArray, zeby obsłużyć formaty Point i Polygon
)

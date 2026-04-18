package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("title") val title: String,
    @SerializedName("events") val events: List<Event>
)

data class Event(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("geometry") val geometries: List<Geometry>
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
)

data class Geometry(
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String, // Najczęściej "Point" lub "Polygon"
    @SerializedName("coordinates") val coordinates: List<Double> // Format [długość, szerokość]
)

package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

/**
 * Główna klasa modelu danych (DTO - Data Transfer Object) reprezentująca odpowiedź z NASA EONET
 * API. Klasa mapuje główny obiekt JSON zwracany przez zapytanie do serwera NASA. Korzysta ze słowa
 * kluczowego `data class` w celu automatycznego wygenerowania metod.
 */
data class EventResponse(
        // Tytuł opisujący źródło danych
        @SerializedName("title") val title: String,
        // Lista wszystkich zdarzeń naturalnych zwróconych przez serwer.
        @SerializedName("events") val events: List<Event>
)

// Reprezentuje pojedyncze zdarzenie naturalne
data class Event(
        // Unikalny identyfikator zdarzenia w systemie NASA (np. "EONET_5112")
        @SerializedName("id") val id: String,
        // Nazwa zdarzenia nadana przez NASA (np. "Wildfire - Los Angeles").
        @SerializedName("title") val title: String,

        // Lista kategorii przypisanych do zdarzenia
        @SerializedName("categories") val categories: List<Category>,
        // Lista obszarów geometrycznych powiązanych z tym zdarzeniem
        @SerializedName("geometry") val geometries: List<Geometry>
)

// Reprezentuje kategorię tematyczną zdarzenia
data class Category(
        // Identyfikator numeryczny lub tekstowy kategorii (np. "wildfires").
        @SerializedName("id") val id: String,
        // nazwa kategorii w języku angielskim
        @SerializedName("title") val title: String
)

// Reprezentuje dane (gdzie i kiedy zdarzenie miało miejsce)
data class Geometry(
        // Data i godzina rejestracji zdarzenia
        @SerializedName("date") val date: String,
        // Typ geometrii zwracany przez NASA (np. "Point" - punkt geograficzny, "Polygon" -
        // wielokąt)
        @SerializedName("type") val type: String,

        // Surowe współrzędne geograficzne w postaci obiektu [JsonArray] z biblioteki Gson.
        // zwraca współrzędne w różnych formatach:
        // - Dla punktu ("Point"): pojedyncza płaska tablica `[długość, szerokość]` (np. `[-120.4,
        // 34.2]`).
        // - Dla linii lub wielokątów ("Polygon", "LineString"): tablica współrzędnych (np. `[[[lng,
        // lat], [lng, lat]]]`)
        @SerializedName("coordinates") val coordinates: JsonArray
)

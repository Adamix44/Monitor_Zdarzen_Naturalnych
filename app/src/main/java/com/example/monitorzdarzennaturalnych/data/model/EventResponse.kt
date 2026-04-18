package com.example.monitorzdarzennaturalnych.data.model

import com.google.gson.annotations.SerializedName

/**
 * Poniższe klasy "data class" w języku Kotlin to struktury przeznaczone do samego przechowywania danych.
 * Dzięki nim nasza aplikacja rozczytuje czysto-tekstowy JSON ze strony serwerów NASA na coś bardziej użytecznego dla nas.
 */

// GŁÓWNY PUNKT - obiekt odpowiedź. Gdy połączy się z wybranym linkiem EONET, 
// to właśnie ta paczka wjeżdża pierwsza i otula w sobie pobraną listę.
data class EventResponse(
    // "@SerializedName" daje do zrozumienia systemowi, żeby wyłapywał z podanego stringu JSON tag o dokładnie takiej nazwie w apostrofach i przypisywał nam pod polską/angielską nazwę zmiennej.
    @SerializedName("title") val title: String, 
    @SerializedName("events") val events: List<Event> // List<Event> oznacza, że po udanym zapytaniu JSON, będzie to kolekcja wydarzeń leżących w środku.
)

// POJEDYNCZE ZDARZENIE na wielkiej liście (np. pojedynczy wulkan, czy konkretna burza nad wodami).
data class Event(
    @SerializedName("id") val id: String, // Każdy pojedynczy wpis z amerykańskiego radaru dysponuje unikalnym kodem z serca platformy do zliczania ID.
    @SerializedName("title") val title: String, // Tytularny ciąg znaków jak np. "Pożar w strefie X"
    @SerializedName("categories") val categories: List<Category>, // Przypisanie zdarzenia, czyli czy to huragan, deszcz, pożar, wulkan wg oddzielnej kategorii.
    @SerializedName("geometry") val geometries: List<Geometry> // Obszar objęty działaniem tego żywiołu.
)

// WYDZIELONA KRYTYKA przypisana do tego samego pożaru. NASA grupuje każdy wpis tagami na kategorie ułatwiając aplikacjom zewnętrznym wyszukiwarki.
data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String
)

// GEOMETRIA POZWALACZ CZYTAJĄCY WSPÓŁRZĘDNE NA ŻYWO (GPS).
// Przetragnij ten model a otrzymasz informację m.in gdzie występuje w formie List<Double> dającej parametry dla naszej Google Maps (Szerokość / Długość).
data class Geometry(
    @SerializedName("date") val date: String, // Kiedy go wylądowała jednostka ratownicza
    @SerializedName("type") val type: String, // Ustawione np. na "Point" dając 1 marker, lub "Polygon" wytyczający obrys płomieni.
    @SerializedName("coordinates") val coordinates: List<Double> // Tablica cyfr: [długość, szerokość] np.: [34.001, -112.50]
)

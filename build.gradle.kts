// To główny (najwyższego poziomu) plik budowy Gradle (Top-level build file).
// Dodajemy tutaj opcje konfiguracyjne wspólne dla całej aplikacji.

plugins {
    // Instalator głównej architektury Androida
    id("com.android.application") version "8.2.2" apply false
    // Dodatek pozwalający traktować kod w "Kotlin" jako ojczysty język systemu Android
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Biblioteka Google pozwalająca aplikacji ukrywać Twój Klucz API z local.properties
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

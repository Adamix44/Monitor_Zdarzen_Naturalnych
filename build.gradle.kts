//glowny plik budowy Gradle
//dodajemy tutaj opcje konfiguracyjne wspolne dla calej aplikacji.
plugins {
    //instalator glownej architektury Androida
    id("com.android.application") version "9.1.1" apply false
    //dodatek pozwalajacy traktowac kod w "Kotlin" jako ojczysty jezyk systemu Android
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    //biblioteka Google pozwalajaca aplikacji ukrywac Klucz API z local.properties
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

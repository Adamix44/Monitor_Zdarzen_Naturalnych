# 🌍 Monitor Zdarzeń Naturalnych

Aplikacja mobilna na system operacyjny **Android** służąca do śledzenia, analizowania oraz wizualizowania w czasie rzeczywistym globalnych anomalii klimatycznych, żywiołów oraz zdarzeń naturalnych rejestrowanych przez satelity naukowe **NASA**.

Projekt integruje rzeczywiste dane satelitarne z zaawansowanym silnikiem map **Google Maps SDK**, prezentując użytkownikowi interaktywny i czytelny panel informacyjny o stanie naszej planety. Całość została zaprojektowana w nowoczesnym stylu **Premium Dark Navy** przy użyciu biblioteki deklaratywnego interfejsu **Jetpack Compose**.

---

## 🚀 Kluczowe Funkcjonalności

Aplikacja została wyposażona w szereg zaawansowanych funkcji ułatwiających analizę anomalii klimatycznych:

*   **Interaktywna Wizualizacja Mapy** – Nanoszenie katastrof w czasie rzeczywistym za pomocą dynamicznych pinezek na mapie terenu Google Maps. Kolory pinezek są dobierane automatycznie w zależności od rodzaju zagrożenia (np. czerwień dla wulkanów i pożarów, pomarańcz dla trzęsień ziemi i powodzi, błękit dla zjawisk lodowych).
*   **Dwuwarstwowy Widok Interfejsu** – Możliwość płynnego przełączania się jednym kliknięciem pomiędzy widokiem interaktywnej mapy a chronologiczną, pionową listą zdarzeń w postaci eleganckich kart informacyjnych.
*   **Zaawansowane Filtrowanie Danych**:
    *   **Filtr Kategori** – Pozwala na zawężenie wyświetlanych zjawisk do konkretnego typu zagrożenia (np. pożary lasów, wulkany, gwałtowne burze, lodowce).
    *   **Filtr Czasowy** – Możliwość wyboru zakresu pobieranych danych z ostatnich 7, 30, 90 lub 365 dni wstecz.
*   **Radar Zagrożeń w Tle (System Alarmów)** – Użytkownik może włączyć stałe monitorowanie w tle. Radar pozwala na wybór zasięgu globalnego (cała planeta) lub lokalnego (określony promień w kilometrach od pozycji użytkownika).
*   **Powiadomienia Push w Czasie Rzeczywistym** – Gdy w tle (nawet przy zamkniętej aplikacji) zostanie wykryta nowa katastrofa naturalna w wybranym zasięgu, aplikacja wyśle natychmiastowe systemowe powiadomienie push z podsumowaniem zdarzenia.
*   **Szczegółowy Panel Zdarzenia** – Dolny wysuwany arkusz (Bottom Sheet) prezentujący kompletne informacje o wybranym zjawisku: spolszczoną kategorię, dokładną datę i godzinę wykrycia, precyzyjne współrzędne geograficzne oraz źródło naukowe NASA EONET.

---

## 🎨 Wygląd i Estetyka (Premium Dark Navy)

Aplikacja została zaprojektowana zgodnie z najnowszymi standardami projektowymi **Material Design 3**:
*   Wykorzystuje autorską paletę barw **Dark Navy** – głębokie granaty połączone z neonowymi akcentami błękitu i turkusu.
*   Integracja z systemowym paskiem statusu Androida – górne i dolne paski systemowe urządzenia automatycznie dostosowują swoją barwę, tworząc spójny i estetyczny interfejs pełnoekranowy (Edge-to-Edge).
*   Płynne animacje – dioda radaru na pasku stanu alarmu pulsuje w tle za pomocą zaawansowanych interpolatorów animacji Compose.

---

## 🏗️ Architektura i Wzorce Projektowe

Aplikacja została zbudowana zgodnie z oficjalnymi wytycznymi Google dla nowoczesnych aplikacji na system Android:

*   **Wzorzec MVVM (Model-View-ViewModel)** – Ścisła separacja logiki biznesowej od warstwy prezentacji. Stan interfejsu jest bezpiecznie przechowywany w ViewModelu i obserwowany przez widoki Compose jako obiekty `LiveData`.
*   **Wzorzec Repository** – Centralna warstwa dostępu do danych, zapewniająca offline-safety oraz izolację źródeł sieciowych.
*   **Kotlin Coroutines** – Wykonywanie ciężkich zadań sieciowych (Retrofit) oraz dyskowych w tle na dedykowanej puli wątków (`Dispatchers.IO`), co gwarantuje 100% płynności działania ekranu głównego.
*   **Android WorkManager** – Cykliczny harmonogram zadań w tle (minimum co 15 minut) obsługujący pobieranie i weryfikację nowych katastrof w tle, odporny na restarts i zamknięcia aplikacji.

---

## 🔌 Integracja z Zewnętrznymi Serwisami (API)

1.  **[NASA EONET API v3](https://eonet.gsfc.nasa.gov/docs/v3) (Earth Observatory Natural Event Tracker)**
    *   Służy do ciągłego pobierania rzeczywistych, naukowych danych na temat katastrof i anomalii naturalnych na globie.
    *   Wdrożono zaawansowany algorytm dekodowania dynamicznych współrzędnych GeoJSON (zarówno pojedynczych punktów, jak i wielokątów).
2.  **Google Maps Platform SDK for Android**
    *   Udostępnia interaktywne mapy satelitarne i ukształtowania terenu.
    *   Zapewnia automatyczne animowanie kamery (płynne najazdy) na nowo pobrane zdarzenia naturalne.

---

## ⚙️ Wymagania i Konfiguracja Projektu

Aby pomyślnie otworzyć i uruchomić projekt w środowisku **Android Studio**:

1.  **Wymagania minimalne**:
    *   Android Studio w wersji obsługującej **Android Gradle Plugin (AGP) 9.1.1** lub nowszy.
    *   Zainstalowane **Android SDK 34** (wersja kompilacji).
    *   Telefon lub emulator z systemem **Android 8.0 (API 24)** lub nowszym.
2.  **Klucze API**:
    *   Projekt wykorzystuje mechanizm wtyczki `secrets-gradle-plugin`, co oznacza, że Twoje klucze API są w pełni bezpieczne i nie zostaną przypadkowo wrzucone na GitHuba.
    *   Aby zdefiniować klucze, utwórz plik `local.properties` w katalogu głównym projektu i dodaj do niego następujące wpisy (podmieniając wartości na własne):
        ```properties
        MAPS_API_KEY=TUTAJ_WPISZ_TWÓJ_KLUCZ_GOOGLE_MAPS
        NASA_API_KEY=DEMO_KEY
        ```

---

## 👥 Autorzy

*   **Adam Rybacki**
*   **Adam Michalak**

# Monitor Zdarzeń Naturalnych

**Monitor Zdarzeń Naturalnych** aplikacja mobilna na system operacyjny **Android**, służąca do monitorowania globalnych anomalii klimatycznych, żywiołów oraz zdarzeń naturalnych w czasie rzeczywistym.

Projekt integruje dane satelitarne udostępniane przez **NASA** z interaktywnym silnikiem map **Google Maps SDK**, prezentując użytkownikowi platformę o stanie naszej planety.

---

## Główne Funkcje Aplikacji

- **Wizualizacja** – Wykorzystuje Google Maps SDK do nanoszenia w czasie rzeczywistym zdarzeń naturalnych za pomocą pinezek.
- **Filtr Czasowy** – Umożliwia użytkownikowi filtrowanie zdarzeń historycznych i aktywnych z ostatnich 7, 30, 90 lub 365 dni.
- **Filtrowanie Kategorii** – Pozwala na filtrowanie zdarzeń według typów
- **Dwuwarstwowy Widok Danych** – Elastyczne przełączanie interfejsu pomiędzy widokiem interaktywnej mapy satelitarnej, a chronologiczną, pionową listą zdarzeń w formie kart.
- **Panel Informacyjny** – Wysuwana od dołu karta prezentująca kompletne szczegóły wybranego zdarzenia.
- **Automatyczny Motyw Ciemny i Jasny** – Pełna integracja z ciemnym motywem systemowym Android.

---

## Architektura i Wzorce Projektowe

- **MVVM (Model-View-ViewModel)**
- **Wzorzec Repository** – Centralny punkt zarządzania źródłami danych.
- **Jetpack Compose**
- **Bezpieczna wielowątkowość (Kotlin Coroutines)**

---

## Integracja z Zewnętrznymi Serwisami (API)

1.  **[NASA EONET API v3](https://eonet.gsfc.nasa.gov/docs/v3) (Earth Observatory Natural Event Tracker)**
    - Służy do ciągłego pobierania rzeczywistych danych na temat katastrof i anomalii naturalnych na globie.
2.  **Google Maps Platform SDK for Android**
    - Udostępnia interaktywne mapy satelitarne.
    - Odpowiada za renderowanie pinezek.

---

## Autorzy

- **Adam Rybacki**
- **Adam Michalak**

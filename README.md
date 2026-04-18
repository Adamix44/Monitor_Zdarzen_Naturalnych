# Monitor_Zdarzen_Naturalnych
Aplikacja na system Android do monitorowania zdarzeń naturalnych na świecie w czasie rzeczywistym. Zaprojektowana w środowisku Android Studio i korzystająca z **NASA EONET API** (Earth Observatory Natural Event Tracker).

## Funkcje

- **Mapa z pinezkami** — zdarzenia naturalne wyświetlane za pomocą Google Maps
- **Filtrowanie po kategorii** — pożary 🔥, sztormy 🌪️, wulkany 🌋, powodzie 🌊
- **Dane w czasie rzeczywistym** — pobieranie aktualnych danych z NASA EONET API
- **Szczegóły zdarzeń** — kliknięcie w pinezkę pokazuje nazwę, kategorię i datę

## Narzędzia

- **Kotlin** — język programowania
- **Retrofit** — komunikacja z API
- **Google Maps SDK** — wyświetlanie mapy
- **Coroutines** — pobieranie danych
- **Material Design** — interfejs użytkownika

## API

Aplikacja korzysta z [NASA EONET API v3](https://eonet.gsfc.nasa.gov/docs/v3):

- Endpoint: `https://eonet.gsfc.nasa.gov/api/v3/events`
- Dane: aktywne zdarzenia naturalne (pożary, sztormy, wulkany, powodzie itp.)

##Autorzy

Adam Rybacki
oraz
Adam Michalak

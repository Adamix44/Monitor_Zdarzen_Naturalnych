# Monitor_Zdarzen_Naturalnych

Celem projektu jest stworzenie aplikacji mobilnej na system operacyjny Android, służącej do monitorowania zdarzeń naturalnych na świecie w czasie rzeczywistym.
Aplikacja gromadzi dane o anomaliach i żywiołach (m.in. pożary lasów, erupcje wulkanów), a następnie prezentuje je w intuicyjny sposób dla użytkownika, nanosząc je na interaktywną mapę satelitarną.

## Funkcje

- **Mapa z pinezkami** — zdarzenia naturalne wyświetlane za pomocą Google Maps
- **Filtrowanie po kategorii** — pożary, sztormy, wulkany, powodzie
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

- Dane: aktywne zdarzenia naturalne (pożary, sztormy, wulkany, powodzie itp.)
  oraz Google Maps SDK for Android - do renderowania natywnej mapy świata i umieszczania współrzędnych geograficznych (pinezek).

## Autorzy

Adam Rybacki
oraz
Adam Michalak

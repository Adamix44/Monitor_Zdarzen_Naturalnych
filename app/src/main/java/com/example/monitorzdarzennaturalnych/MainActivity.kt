package com.example.monitorzdarzennaturalnych

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.monitorzdarzennaturalnych.repository.EventRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

/**
 * MainActivity to główny ekran aplikacji (klasa dziedzicząca po AppCompatActivity).
 * Jest to pierwsze okno pojawiające się po kliknięciu ikony aplikacji w telefonie.
 * Implementacja `OnMapReadyCallback` pozwala na wykonanie kodu dopiero gdy mapa 
 * Google jest w pełni załadowana i gotowa do użycia.
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // Zmienne przypisywane później (lateinit), ponieważ są gotowe dopiero po stworzeniu interfejsu (metoda onCreate).
    private lateinit var mMap: GoogleMap
    // Zmienna repository zapewnia dostęp do naszych pobranych z internetu wydarzeń.
    private val repository = EventRepository()
    private lateinit var progressBar: ProgressBar

    /**
     * Główna funkcja wywoływana przy starcie tego okna (Activity).
     * Służy m.in. do ustawienia widoku aplikacji (nasz wpisany layout activity_main.xml).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Znajdujemy okrąg ładujący po jego ID zadeklarowanym w XML, aby móc zarządzać jego znikaniem/pojawianiem.
        progressBar = findViewById(R.id.progressBar)

        // Odnajdujemy fragment mapy i prosimy system (getMapAsync) o przygotowanie mapy. 
        // Interfejs powiadomi klasę, gdy tylko przygotuje kafelki.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Callback uaktywnia się automatycznie w sekundzie, gdy biblioteki Google wgrają mapę.
     * Przekazuje nam jej pełnoprawny, powołany obiekt (googleMap).
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // Ustawiamy domyślny punkt centralny uruchomienia – czyli całą szerokość Ziemi (punkt w oceanie 0.0, 0.0)
        val worldCenter = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(worldCenter, 2f)) // 2f oznacza stopień oddalenia "zoomu"

        // Skoro mapa już bezpiecznie wyświetla się na ekranie, prosimy z innej funkcji o rozpoczęcie nakładania na nią pinezek z NASA.
        loadEvents()
    }

    /**
     * Funkcja zajmująca się połączeniem pobierania z aktualizacją mapy.
     */
    private fun loadEvents() {
        // Okazuje w tym momencie animowane kółko wczytywania żeby użytkownik wiedział że trwa połączenie
        progressBar.visibility = View.VISIBLE
        
        // Operacje z siecią (pobieranie powolnego gigabajtowego tekstu) odbywające się obok normalnego działania interfejsu (więc przyciski użytkownika nadal reagują).
        lifecycleScope.launch {
            // Blokada w kodzie dopóki internet nie odda nam gotowej rozczytanej Listy wydarzen NASA
            val events = repository.getEvents() 
            // Skoro mamy listę (lub jej brak na wypadek błędu), wyłączamy kółko animacji.
            progressBar.visibility = View.GONE
            
            // Reagujemy na brak internetu/brak danych
            if (events.isEmpty()) {
                // Toast to małe powiadomienie-dymek które pojawia się u dołu telefonu.
                Toast.makeText(this@MainActivity, "Brak wydarzeń lub błąd API.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Mamy już informacje z NASA - wykonujemy operacje dla KAŻDEGO z listy pojedyńczych wydarzeń (wulkanu/pożaru).
            for (event in events) {
                // Skupiamy się na weryfikacji czy NASA dołączyła koordynaty (Geometrie) i czy jest niepuste.
                if (event.geometries.isNotEmpty()) {
                    val geo = event.geometries.first() // Bierzemy pierwszą, zdefiniowaną współrzędną (jeden konkretny punkt z wielu w pożarze)
                    
                    // Zabezpieczamy konwersję długości geograficznej. 
                    // NASA z zasady oddaje nam listę dwuznakową [Longitude (Długość), Latitude (Szerokość)]
                    if (geo.coordinates.size >= 2) {
                        val lat = geo.coordinates[1] // Z punktu NASA index [1] to nasza ostateczna szerokość (Latitude)
                        val lng = geo.coordinates[0] // z wylotu NASA index [0] to Długość (Longitude)
                        
                        // Mapy Google korzystają z gotowego obiektu zawierającego te dwie zmienne typu Float. Tworzymy go:
                        val position = LatLng(lat, lng)
                        
                        // Pobieramy kategorię zdarzenia – np "Wildfires" do podtytułu na pinezce (używa elementu domyślnego, w razie błędu pisze Nieznane)
                        val categoryTitle = if (event.categories.isNotEmpty()) event.categories.first().title else "Nieznane"

                        // System prosi fragment Mapy o położenie Pinezki oraz o dorysowanie nad nią tekstu tytulowego uaktywniającego się po uderzenia palcem użytkownika.
                        mMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(event.title) // Np. nazwa miejscowości z "Volcano"
                                .snippet("Kategoria: $categoryTitle | Data: ${geo.date}") // snippet czyli krótki tekst wyciągający opisy, na dole od tytułu
                        )
                    }
                }
            }
        }
    }
}

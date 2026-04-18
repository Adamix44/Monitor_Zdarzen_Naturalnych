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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val repository = EventRepository()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)

        // Rozwijamy fragment mapy i przypisujemy mu widoczność
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // Domyślnie kamerę ustawiamy na widok całego globu (przybliżenie ok. 2f)
        val worldCenter = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(worldCenter, 2f))

        // Uruchamiamy pobieranie pinezek zaraz po załadowaniu Mapy
        loadEvents()
    }

    private fun loadEvents() {
        progressBar.visibility = View.VISIBLE
        
        // Rozpoczynamy współbieżny wątek w cyklu życia aplikacji aby nie opóźniać Mapy
        lifecycleScope.launch {
            val events = repository.getEvents() // prośba o odpowiedź do serwera Repozytorium
            progressBar.visibility = View.GONE
            
            if (events.isEmpty()) {
                Toast.makeText(this@MainActivity, "Brak wydarzeń lub błąd API.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            for (event in events) {
                if (event.geometries.isNotEmpty()) {
                    val geo = event.geometries.first()
                    // Zabezpieczamy konwersję długości geograficznej. 
                    // NASA oddaje listę Coordinates w formie [longitude, latitude]
                    if (geo.coordinates.size >= 2) {
                        val lat = geo.coordinates[1] // szerokość
                        val lng = geo.coordinates[0] // długość
                        val position = LatLng(lat, lng)
                        
                        val categoryTitle = if (event.categories.isNotEmpty()) event.categories.first().title else "Nieznane"

                        // Stawiamy pinezkę Google Maps!
                        mMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(event.title)
                                .snippet("Kategoria: $categoryTitle | Data: ${geo.date}")
                        )
                    }
                }
            }
        }
    }
}

package com.example.monitorzdarzennaturalnych

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.monitorzdarzennaturalnych.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    // podlaczenie widoku MVVM
    private val viewModel: MainViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    // glowna funkcja przy starcie okna
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        // wczytanie fragmentu z mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // gdy mapa gotowa do uzycia
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // srodek globu
        val worldCenter = LatLng(0.0, 0.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(worldCenter, 2f))

        // nakazujemy zeby dane o zdarzeniach byly pobierane po wczytaniu mapy
        progressBar.visibility = View.VISIBLE
        viewModel.loadEvents()
        viewModel.events.observe(this) { events ->
            progressBar.visibility = View.GONE
            // blad pobierania
            if (events.isEmpty()) {
                Toast.makeText(this@MainActivity, "Brak wydarzen lub blad API.", Toast.LENGTH_SHORT)
                        .show()
                return@observe
            }

            for (event in events) {
                if (event.geometries.isNotEmpty()) {
                    val geo = event.geometries.first()
                    // dlugosc, szerokosc
                    if (geo.coordinates.size >= 2) {
                        val lat = geo.coordinates[1]
                        val lng = geo.coordinates[0]
                        val position = LatLng(lat, lng)
                        val categoryTitle =
                                if (event.categories.isNotEmpty()) event.categories.first().title
                                else "Nieznane"
                        // nalozenie pinezki z tytulem
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

package com.example.monitorzdarzennaturalnych

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.monitorzdarzennaturalnych.viewmodel.MainViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    
    // podlaczenie widoku MVVM
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Zlecamy pobieranie danych przy starcie
        viewModel.loadEvents()

        // Zastepstwo dla starego XML - otwieramy swiat Jetpack Compose
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MonitorZdarzenScreen(viewModel)
                }
            }
        }
    }
}

// Deklaratywna funkcja budujaca caly interfejs z Mapą
@Composable
fun MonitorZdarzenScreen(viewModel: MainViewModel) {
    // Obserwowanie strumienia LiveData z ViewModelu
    val events by viewModel.events.observeAsState(initial = emptyList())
    val context = LocalContext.current
    
    // Ustawienie srodka swiata dla nowej mapy
    val defaultCameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    // Box pozwala nakladac elementy na siebie (np. kolko na mape)
    Box(modifier = Modifier.fillMaxSize()) {
        
        // Komponent nowoczesnej Mapy Google
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = defaultCameraPosition
        ) {
            // Nakladanie pinezek bezposrednio w bloku Mapy
            for (event in events) {
                if (event.geometries.isNotEmpty()) {
                    val geo = event.geometries.first()
                    if (geo.coordinates.size >= 2) {
                        val lat = geo.coordinates[1]
                        val lng = geo.coordinates[0]
                        val position = LatLng(lat, lng)
                        
                        val categoryTitle = if (event.categories.isNotEmpty()) event.categories.first().title else "Nieznane"

                        // Nowoczesny "Marker" z biblioteki maps-compose
                        Marker(
                            state = MarkerState(position = position),
                            title = event.title,
                            snippet = "Kategoria: $categoryTitle | Data: ${geo.date}"
                        )
                    }
                }
            }
        }

        // Automatyczne pokazywanie kolka na srodku podczas sciagania danych
        if (events.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

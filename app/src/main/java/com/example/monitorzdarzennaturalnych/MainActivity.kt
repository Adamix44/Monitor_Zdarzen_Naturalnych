package com.example.monitorzdarzennaturalnych

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.ui.components.*
import com.example.monitorzdarzennaturalnych.ui.theme.*
import com.example.monitorzdarzennaturalnych.viewmodel.MainViewModel
import com.example.monitorzdarzennaturalnych.viewmodel.translateCategory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Żądanie uprawnienia do powiadomień (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED
            ) {
                val launcher =
                        registerForActivityResult(
                                ActivityResultContracts.RequestPermission()
                        ) { /* wynik nie blokuje działania */}
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        viewModel.loadEvents()

        setContent { MonitorTheme { MonitorZdarzenApp(viewModel) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorZdarzenApp(viewModel: MainViewModel) {
    val events by viewModel.events.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val isListView by viewModel.isListView.observeAsState(initial = false)
    val categories by viewModel.availableCategories.observeAsState(initial = listOf("Wszystkie"))
    val selectedCategory by viewModel.selectedCategory.observeAsState(initial = "Wszystkie")
    val selectedDays by viewModel.selectedDays.observeAsState(initial = 30)
    val selectedEvent by viewModel.selectedEvent.observeAsState(initial = null)
    val alarmEnabled by viewModel.alarmEnabled.observeAsState(initial = false)
    val alarmRadiusKm by viewModel.alarmRadiusKm.observeAsState(initial = 0)

    val sheetState = rememberModalBottomSheetState()
    var showAlarmDialog by remember { mutableStateOf(value = false) }

    Box(modifier = Modifier.fillMaxSize().background(NavyDark)) {
        if (isListView) {
            // Widok Listy: Układ sekwencyjny (jeden pod drugim)
            Column(modifier = Modifier.fillMaxSize()) {
                MonitorTopBar(
                        alarmEnabled = alarmEnabled,
                        isListView = isListView,
                        onAlarmClick = {
                            if (alarmEnabled) viewModel.disableAlarm()
                            else showAlarmDialog = true
                        }
                ) {
                    viewModel.setListView(isList = false)
                }

                if (alarmEnabled) {
                    AlarmStatusBar(
                            radiusKm = alarmRadiusKm,
                            onDisable = { viewModel.disableAlarm() }
                    )
                }

                FiltersSection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.setCategory(it) },
                        selectedDays = selectedDays,
                        onDaysSelected = { viewModel.setDays(it) }
                )

                EventsListScreen(events, onEventClick = { viewModel.selectEvent(it) })
            }
        } else {
            // Widok Mapy: Układ warstwowy (filtry nad mapą)
            EventsMapScreen(events, onEventClick = { viewModel.selectEvent(it) })

            Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                MonitorTopBar(
                        alarmEnabled = alarmEnabled,
                        isListView = isListView,
                        onAlarmClick = {
                            if (alarmEnabled) viewModel.disableAlarm()
                            else showAlarmDialog = true
                        }
                ) {
                    viewModel.setListView(isList = true)
                }

                if (alarmEnabled) {
                    AlarmStatusBar(
                            radiusKm = alarmRadiusKm,
                            onDisable = { viewModel.disableAlarm() }
                    )
                }

                FiltersSection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.setCategory(it) },
                        selectedDays = selectedDays,
                        onDaysSelected = { viewModel.setDays(it) }
                )
            }

            // Floating controls (prawy dół)
            Column(
                    modifier =
                            Modifier.align(Alignment.BottomEnd)
                                    .padding(end = 16.dp, bottom = 24.dp)
                                    .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val count = events.size
                if (count > 0) {
                    Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = NavyElevated.copy(alpha = 0.9f),
                            tonalElevation = 4.dp
                    ) {
                        Text(
                                text = "$count zdarzeń",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                        )
                    }
                }
            }
        }

        // Wskaźnik ładowania
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                        color = AccentBlue,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(40.dp)
                )
            }
        } else if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NavySurfaceVariant.copy(alpha = 0.9f)
                ) {
                    Text(
                            text = "Brak zdarzeń dla tych filtrów",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                    )
                }
            }
        }
    }

    // pasek dolny - szczegóły zdarzenia
    if (selectedEvent != null) {
        ModalBottomSheet(
                onDismissRequest = { viewModel.selectEvent(null) },
                sheetState = sheetState,
                containerColor = NavySurface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = {
                    Box(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
                        Box(
                                modifier =
                                        Modifier.width(40.dp)
                                                .height(4.dp)
                                                .clip(CircleShape)
                                                .background(TextTertiary)
                        )
                    }
                }
        ) { EventDetailsSheet(event = selectedEvent!!) }
    }
    if (showAlarmDialog) {
        AlarmSetupDialog(
                onDismiss = { showAlarmDialog = false },
                onConfirm = { radiusKm ->
                    viewModel.enableAlarm(radiusKm)
                    showAlarmDialog = false
                }
        )
    }
}

// Funkcja pomocnicza dekodująca złożone struktury JSON na współrzędne
fun parseLatLng(coords: com.google.gson.JsonArray): LatLng? {
    try {
        if ((coords.size() == 2) && (coords[0].isJsonPrimitive)) {
            return LatLng(coords[1].asDouble, coords[0].asDouble)
        } else {
            var current = coords
            while ((current.size() > 0) && current[0].isJsonArray) {
                current = current[0].asJsonArray
            }
            if ((current.size() >= 2) && current[0].isJsonPrimitive) {
                return LatLng(current[1].asDouble, current[0].asDouble)
            }
        }
    } catch (_: Exception) {}
    return null
}

// Ekran mapy z pinezkami
@Composable
fun EventsMapScreen(events: List<Event>, onEventClick: (Event) -> Unit) {
    val initialPosition =
            remember(events) {
                if (events.isNotEmpty() && events.first().geometries.isNotEmpty()) {
                    parseLatLng(events.first().geometries.first().coordinates) ?: LatLng(0.0, 0.0)
                } else {
                    LatLng(0.0, 0.0)
                }
            }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 3f)
    }

    // ustawienie widoku mapy na pierwsze zdarzenie
    LaunchedEffect(events) {
        if (events.isNotEmpty() && events.first().geometries.isNotEmpty()) {
            parseLatLng(events.first().geometries.first().coordinates)?.let { location ->
                cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(location, 4f),
                        durationMs = 1500
                )
            }
        }
    }

    GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.TERRAIN),
            uiSettings =
                    MapUiSettings(
                            zoomControlsEnabled = false,
                            compassEnabled = true,
                            mapToolbarEnabled = false
                    )
    ) {
        events.forEach { event ->
            if (event.geometries.isNotEmpty()) {
                val geo = event.geometries.first()
                val latLng = parseLatLng(geo.coordinates)

                if (latLng != null) {
                    val catTitle =
                            if (event.categories.isNotEmpty())
                                    translateCategory(event.categories.first().title)
                            else ""

                    // Kolory pinezek zależne od typu zagrożenia
                    val hue =
                            when {
                                catTitle.contains("Pożary", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_RED
                                catTitle.contains("Wulkany", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_RED
                                catTitle.contains("Trzęsienia", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_ORANGE
                                catTitle.contains("Powodzie", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_ORANGE
                                catTitle.contains("burze", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_ORANGE
                                catTitle.contains("Lód", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_AZURE
                                catTitle.contains("Śnieżyce", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_AZURE
                                catTitle.contains("Ekstremalne", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_CYAN
                                catTitle.contains("Susze", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_GREEN
                                catTitle.contains("Zadymienie", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_GREEN
                                catTitle.contains("Zabarwienia", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_GREEN
                                catTitle.contains("Osuwiska", ignoreCase = true) ->
                                        BitmapDescriptorFactory.HUE_ORANGE
                                else -> BitmapDescriptorFactory.HUE_VIOLET
                            }

                    Marker(
                            state = MarkerState(position = latLng),
                            title = event.title,
                            snippet = catTitle,
                            icon = BitmapDescriptorFactory.defaultMarker(hue),
                            onClick = {
                                onEventClick(event)
                                true
                            }
                    )
                }
            }
        }
    }
}

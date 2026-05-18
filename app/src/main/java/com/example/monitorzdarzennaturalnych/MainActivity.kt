package com.example.monitorzdarzennaturalnych

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.ui.theme.MonitorTheme
import com.example.monitorzdarzennaturalnych.viewmodel.MainViewModel
import com.example.monitorzdarzennaturalnych.viewmodel.translateCategory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Żądanie uprawnienia do powiadomień (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                val launcher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { /* wynik nie blokuje działania */ }
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        viewModel.loadEvents()

        setContent {
            MonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MonitorZdarzenApp(viewModel)
                }
            }
        }
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

    // Stan dialogu ustawiania alarmu
    var showAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitor Zdarzeń Naturalnych", fontWeight = FontWeight.Bold) },
                actions = {
                    // Przycisk Alarmu
                    IconButton(onClick = {
                        if (alarmEnabled) {
                            viewModel.disableAlarm()
                        } else {
                            showAlarmDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = if (alarmEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = if (alarmEnabled) "Wyłącz alarm" else "Ustaw alarm",
                            tint = if (alarmEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Przełącznik Widok Listy / Mapa (Opcja C)
                    IconButton(onClick = { viewModel.setListView(!isListView) }) {
                        Icon(
                            imageVector = if (isListView) Icons.Default.Map else Icons.Default.List,
                            contentDescription = "Przełącz widok"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Informacja o aktywnym alarmie
            if (alarmEnabled) {
                AlarmStatusBar(radiusKm = alarmRadiusKm, onDisable = { viewModel.disableAlarm() })
            }
            
            // Komponent filtrów poziomo (Kategorie i Czas)
            FiltersSection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setCategory(it) },
                selectedDays = selectedDays,
                onDaysSelected = { viewModel.setDays(it) }
            )

            // Główny kontener (Mapa lub Lista)
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                if (isListView) {
                    EventsListScreen(events, onEventClick = { viewModel.selectEvent(it) })
                } else {
                    EventsMapScreen(events, onEventClick = { viewModel.selectEvent(it) })
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (events.isEmpty() && !isLoading) {
                    Text(
                        text = "Brak zdarzeń dla tych filtrów.",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }

    // Otwieranie szczegółów wybranego wydarzenia (Bottom Sheet)
    if (selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectEvent(null) },
            sheetState = sheetState
        ) {
            EventDetailsSheet(event = selectedEvent!!)
        }
    }

    // Dialog ustawiania alarmu
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

// --- Komponenty Alarmu ---

@Composable
fun AlarmStatusBar(radiusKm: Int, onDisable: () -> Unit) {
    val rangeText = if (radiusKm == 0) "cała planeta" else "$radiusKm km"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Alarm aktywny — zasięg: $rangeText",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            TextButton(onClick = onDisable) {
                Text("Wyłącz", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AlarmSetupDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var radiusText by remember { mutableStateOf("") }
    var useGlobal by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ustaw alarm", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Otrzymasz powiadomienie, gdy pojawi się nowe zdarzenie naturalne.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = useGlobal, onClick = { useGlobal = true })
                    Text("Cała planeta", modifier = Modifier.clickable { useGlobal = true })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !useGlobal, onClick = { useGlobal = false })
                    Text("Własny zasięg (km)", modifier = Modifier.clickable { useGlobal = false })
                }

                if (!useGlobal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = radiusText,
                        onValueChange = { radiusText = it.filter { c -> c.isDigit() } },
                        label = { Text("Zasięg w km") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val radius = if (useGlobal) 0 else (radiusText.toIntOrNull() ?: 0)
                onConfirm(radius)
            }) {
                Text("Ustaw alarm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

// --- Komponenty UI ---

@Composable
fun FiltersSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    selectedDays: Int,
    onDaysSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        // Filtr Czasu (Opcja A)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Pokaż wydarzenia z:", fontWeight = FontWeight.SemiBold)
            val daysOptions = listOf(7, 30, 90, 365)
            
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Ostatnie $selectedDays dni")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    daysOptions.forEach { days ->
                        DropdownMenuItem(
                            text = { Text("Ostatnie $days dni") },
                            onClick = {
                                onDaysSelected(days)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Pasek kategorii (Przewijany poziomo)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }
    }
}

// Funkcja pomocnicza dekodujaca zlozone struktury JSON na wspolrzedne
fun parseLatLng(coords: com.google.gson.JsonArray): LatLng? {
    try {
        if (coords.size() == 2 && coords[0].isJsonPrimitive) {
            return LatLng(coords[1].asDouble, coords[0].asDouble)
        } else {
            var current = coords
            while (current.size() > 0 && current[0].isJsonArray) {
                current = current[0].asJsonArray
            }
            if (current.size() >= 2 && current[0].isJsonPrimitive) {
                return LatLng(current[1].asDouble, current[0].asDouble)
            }
        }
    } catch (e: Exception) {}
    return null
}

@Composable
fun EventsMapScreen(events: List<Event>, onEventClick: (Event) -> Unit) {
    // Kamera domyslnie na srodek, ale jesli sa zdarzenia, przesuwamy ja na pierwsze z nich
    val initialPosition = remember(events) {
        if (events.isNotEmpty() && events.first().geometries.isNotEmpty()) {
            parseLatLng(events.first().geometries.first().coordinates) ?: LatLng(0.0, 0.0)
        } else {
            LatLng(0.0, 0.0)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 3f)
    }

    // Automatyczne przesuniecie kamery gdy zaktualizuja sie zdarzenia
    LaunchedEffect(events) {
        if (events.isNotEmpty() && events.first().geometries.isNotEmpty()) {
            val firstLocation = parseLatLng(events.first().geometries.first().coordinates)
            if (firstLocation != null) {
                cameraPositionState.animate(
                    update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(firstLocation, 4f),
                    durationMs = 1500
                )
            }
        }
    }

    android.util.Log.d("MONITOR", "EventsMapScreen: otrzymano ${events.size} zdarzen")

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.TERRAIN)
    ) {
        events.forEach { event ->
            if (event.geometries.isNotEmpty()) {
                val geo = event.geometries.first()
                val latLng = parseLatLng(geo.coordinates)

                android.util.Log.d("MONITOR", "Zdarzenie: ${event.title}, geometries: ${event.geometries.size}, coords raw: ${geo.coordinates}, parsed: $latLng")

                if (latLng != null) {
                    val catTitle = if (event.categories.isNotEmpty()) translateCategory(event.categories.first().title) else ""

                    val hue = when {
                        catTitle.contains("Pożary", ignoreCase = true) -> BitmapDescriptorFactory.HUE_ORANGE
                        catTitle.contains("Wulkany", ignoreCase = true) -> BitmapDescriptorFactory.HUE_ROSE
                        catTitle.contains("burze", ignoreCase = true) -> BitmapDescriptorFactory.HUE_AZURE
                        catTitle.contains("Lód", ignoreCase = true) -> BitmapDescriptorFactory.HUE_CYAN
                        else -> BitmapDescriptorFactory.HUE_RED
                    }

                    Marker(
                        state = MarkerState(position = latLng),
                        title = event.title,
                        snippet = "Kategoria: $catTitle",
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

@Composable
fun EventsListScreen(events: List<Event>, onEventClick: (Event) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onEventClick(event) },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    val catTitle = if (event.categories.isNotEmpty()) translateCategory(event.categories.first().title) else "Nieznana Kategoria"
                    val date = if (event.geometries.isNotEmpty()) event.geometries.first().date else "Brak daty"
                    Text(text = "Kategoria: $catTitle", color = MaterialTheme.colorScheme.primary)
                    Text(text = "Zanotowano: $date", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// Dolny Panel Szczegółów (Bottom Sheet)
@Composable
fun EventDetailsSheet(event: Event) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        
        val catTitle = if (event.categories.isNotEmpty()) translateCategory(event.categories.first().title) else "Nieznana Kategoria"
        Text(text = "Kategoria: $catTitle", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (event.geometries.isNotEmpty()) {
            val geo = event.geometries.first()
            Text(text = "Data wykrycia: ${geo.date}", fontSize = 16.sp)
            val parsedCoords = parseLatLng(geo.coordinates)
            if (parsedCoords != null) {
                Text(text = "Pozycja: LAT ${parsedCoords.latitude}, LNG ${parsedCoords.longitude}", fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Dane pobrane bezpośrednio z satelitów NASA Earth Observatory.", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

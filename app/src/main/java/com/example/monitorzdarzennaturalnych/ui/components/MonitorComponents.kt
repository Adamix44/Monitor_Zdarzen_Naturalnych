package com.example.monitorzdarzennaturalnych.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.ui.theme.*
import com.example.monitorzdarzennaturalnych.viewmodel.translateCategory

// ─── Top Bar ───

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorTopBar(
    alarmEnabled: Boolean,
    isListView: Boolean,
    onAlarmClick: () -> Unit,
    onViewToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceOverlay,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikona radaru
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentBlue, AccentTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nazwa aplikacji
            Text(
                text = "Monitor Zdarzeń",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            // Alarm
            FilledIconButton(
                onClick = onAlarmClick,
                modifier = Modifier.size(38.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (alarmEnabled) AccentBlue.copy(alpha = 0.15f) else Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (alarmEnabled) Icons.Filled.Notifications else Icons.Outlined.NotificationsNone,
                    contentDescription = if (alarmEnabled) "Wyłącz alarm" else "Ustaw alarm",
                    tint = if (alarmEnabled) AccentBlue else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Przełącznik widoku
            FilledIconButton(
                onClick = onViewToggle,
                modifier = Modifier.size(38.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isListView) Icons.Outlined.Map else Icons.Outlined.ViewList,
                    contentDescription = "Przełącz widok",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Alarm Status Bar ───

@Composable
fun AlarmStatusBar(radiusKm: Int, onDisable: () -> Unit) {
    val rangeText = if (radiusKm == 0) "cała planeta" else "$radiusKm km"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = NavyElevated,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentTeal.copy(alpha = pulseAlpha))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Alarm · zasięg: $rangeText",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            TextButton(
                onClick = onDisable,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
            ) {
                Text("Wyłącz", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// ─── Filters Section (Chips + Time Dropdown) ───

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    selectedDays: Int,
    onDaysSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Chipy kategorii (teraz na górze)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) ChipSelected else ChipDefault,
                    animationSpec = tween(250), label = "chip"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) AccentBlue.copy(alpha = 0.6f) else Color(0xFF2E3558),
                    animationSpec = tween(250), label = "border"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) AccentBlueBright else TextSecondary,
                    animationSpec = tween(250), label = "text"
                )

                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            category,
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = bgColor,
                        selectedContainerColor = bgColor,
                        labelColor = textColor,
                        selectedLabelColor = textColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = borderColor,
                        selectedBorderColor = borderColor,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.5.dp
                    ),
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null
                )
            }
        }

        // Pasek: chip czasu (teraz na dole)
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chip czasu (dropdown)
            val daysOptions = listOf(7, 30, 90, 365)
            var expanded by remember { mutableStateOf(false) }

            Box {
                AssistChip(
                    onClick = { expanded = true },
                    label = {
                        Text(
                            "${selectedDays}d",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = NavySurfaceVariant,
                        labelColor = TextPrimary,
                        leadingIconContentColor = AccentBlue
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = Color(0xFF2E3558),
                        borderWidth = 1.dp
                    )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(NavyElevated)
                ) {
                    daysOptions.forEach { days ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Ostatnie $days dni",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (days == selectedDays) AccentBlue else TextPrimary
                                )
                            },
                            onClick = {
                                onDaysSelected(days)
                                expanded = false
                            },
                            leadingIcon = if (days == selectedDays) {
                                {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

// ─── Events List Screen ───

@Composable
fun EventsListScreen(events: List<Event>, onEventClick: (Event) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(events) { event ->
            val catTitle = if (event.categories.isNotEmpty()) translateCategory(event.categories.first().title) else "Nieznana"
            val markerColor = getEventColor(catTitle)

            Card(
                modifier = Modifier.fillMaxWidth().clickable { onEventClick(event) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Kolor typu zdarzenia
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(markerColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getEventIcon(catTitle),
                            contentDescription = null,
                            tint = markerColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val date = if (event.geometries.isNotEmpty()) event.geometries.first().date.take(10) else "—"
                        Text(
                            text = "$catTitle · $date",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─── Event Details Bottom Sheet ───

@Composable
fun EventDetailsSheet(event: Event) {
    val catTitle = if (event.categories.isNotEmpty()) translateCategory(event.categories.first().title) else "Nieznana Kategoria"
    val markerColor = getEventColor(catTitle)

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Nagłówek z kolorem
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(markerColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getEventIcon(catTitle),
                    contentDescription = null,
                    tint = markerColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = catTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = markerColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFF2E3558), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        if (event.geometries.isNotEmpty()) {
            val geo = event.geometries.first()
            DetailRow(Icons.Outlined.CalendarMonth, "Data wykrycia", geo.date.take(16))
            Spacer(modifier = Modifier.height(10.dp))

            val parsedCoords = com.example.monitorzdarzennaturalnych.parseLatLng(geo.coordinates)
            if (parsedCoords != null) {
                DetailRow(
                    Icons.Outlined.LocationOn, "Pozycja",
                    "%.4f, %.4f".format(parsedCoords.latitude, parsedCoords.longitude)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Źródło: NASA Earth Observatory (EONET)",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}

// ─── Alarm Setup Dialog ───

@Composable
fun AlarmSetupDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var radiusText by remember { mutableStateOf("") }
    var useGlobal by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = NavyElevated,
        title = {
            Text(
                "Ustaw alarm",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    "Otrzymasz powiadomienie, gdy pojawi się nowe zdarzenie naturalne.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = useGlobal,
                        onClick = { useGlobal = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentBlue,
                            unselectedColor = TextSecondary
                        )
                    )
                    Text("Cała planeta", color = TextPrimary, modifier = Modifier.clickable { useGlobal = true })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !useGlobal,
                        onClick = { useGlobal = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentBlue,
                            unselectedColor = TextSecondary
                        )
                    )
                    Text("Własny zasięg (km)", color = TextPrimary, modifier = Modifier.clickable { useGlobal = false })
                }

                if (!useGlobal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = radiusText,
                        onValueChange = { radiusText = it.filter { c -> c.isDigit() } },
                        label = { Text("Zasięg w km") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Color(0xFF2E3558),
                            cursorColor = AccentBlue,
                            focusedLabelColor = AccentBlue,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val radius = if (useGlobal) 0 else (radiusText.toIntOrNull() ?: 0)
                    onConfirm(radius)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Ustaw alarm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = TextSecondary)
            }
        }
    )
}

// ─── Helpers ───

fun getEventColor(catTitle: String): Color = when {
    catTitle.contains("Pożary", ignoreCase = true) -> DangerRed
    catTitle.contains("Wulkany", ignoreCase = true) -> DangerRed
    catTitle.contains("Trzęsienia", ignoreCase = true) -> WarningOrange
    catTitle.contains("Powodzie", ignoreCase = true) -> WarningOrange
    catTitle.contains("burze", ignoreCase = true) -> WarningOrange
    catTitle.contains("Lód", ignoreCase = true) -> IceBlue
    catTitle.contains("Śnieżyce", ignoreCase = true) -> IceBlue
    catTitle.contains("Ekstremalne", ignoreCase = true) -> IceBlue
    catTitle.contains("Susze", ignoreCase = true) -> WeatherGreen
    catTitle.contains("Zadymienie", ignoreCase = true) -> WeatherGreen
    catTitle.contains("Zabarwienia", ignoreCase = true) -> WeatherGreen
    catTitle.contains("Osuwiska", ignoreCase = true) -> WarningOrange
    else -> AccentBlue
}

fun getEventIcon(catTitle: String): androidx.compose.ui.graphics.vector.ImageVector = when {
    catTitle.contains("Pożary", ignoreCase = true) -> Icons.Filled.LocalFireDepartment
    catTitle.contains("Wulkany", ignoreCase = true) -> Icons.Filled.Whatshot
    catTitle.contains("burze", ignoreCase = true) -> Icons.Filled.Thunderstorm
    catTitle.contains("Lód", ignoreCase = true) -> Icons.Filled.AcUnit
    catTitle.contains("Śnieżyce", ignoreCase = true) -> Icons.Filled.AcUnit
    catTitle.contains("Powodzie", ignoreCase = true) -> Icons.Filled.Water
    catTitle.contains("Trzęsienia", ignoreCase = true) -> Icons.Filled.Warning
    catTitle.contains("Susze", ignoreCase = true) -> Icons.Filled.WbSunny
    catTitle.contains("Zadymienie", ignoreCase = true) -> Icons.Filled.Cloud
    else -> Icons.Filled.Place
}

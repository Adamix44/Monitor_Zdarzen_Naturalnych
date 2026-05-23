package com.example.monitorzdarzennaturalnych.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.monitorzdarzennaturalnych.R
import com.example.monitorzdarzennaturalnych.data.AlarmPreferences
import com.example.monitorzdarzennaturalnych.data.model.Event
import com.example.monitorzdarzennaturalnych.repository.EventRepository
import com.example.monitorzdarzennaturalnych.viewmodel.translateCategory
import kotlin.math.*

/**
 * Worker wykonujący cykliczne zadania w tle za pomocą WorkManager.
 * Odpowiada za:
 * - Pobranie najnowszych zdarzeń naturalnych z ostatnich 7 dni w tle
 * - Odfiltrowanie zdarzeń, o których użytkownik został już powiadomiony (porównanie ID)
 * - Obliczenie dystansu przy użyciu wzoru Haversine, jeśli ustawiony jest promień geograficzny
 * - Wysłanie powiadomienia systemowego o nowych istotnych zdarzeniach
 */
class EventAlarmWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val CHANNEL_ID = "event_alarm_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "event_alarm_periodic"
        private const val TAG = "EventAlarmWorker"
        private const val EARTH_RADIUS_KM = 6371.0 // Promień Ziemi do wzoru Haversine
    }

    /**
     * Główny cykl pracy workera wywoływany przez system Android.
     */
    override suspend fun doWork(): Result {
        val prefs = AlarmPreferences(applicationContext)

        // Jeżeli alarm został wyłączony w preferencjach, kończymy pracę
        if (!prefs.alarmEnabled) {
            return Result.success()
        }

        return try {
            val repository = EventRepository()
            // Pobieramy zdarzenia z ostatnich 7 dni w tle
            val allEvents = repository.getEvents(7)

            val radiusKm = prefs.radiusKm
            val userLat = prefs.userLat
            val userLng = prefs.userLng
            val knownIds = prefs.lastCheckedEventIds

            // Filtrujemy tylko te zdarzenia, których jeszcze nie powiadamialiśmy
            val newEvents = allEvents.filter { it.id !in knownIds }

            // Filtrujemy geograficznie, jeśli ustawiony jest promień lokalny
            val relevantEvents = if (radiusKm > 0) {
                newEvents.filter { event ->
                    isEventInRadius(event, userLat, userLng, radiusKm.toDouble())
                }
            } else {
                newEvents
            }

            // Jeśli znaleziono nowe ważne zdarzenia, wysyłamy powiadomienie
            if (relevantEvents.isNotEmpty()) {
                sendNotification(relevantEvents)
            }

            // Zapisujemy listę ID jako sprawdzoną, aby zapobiec duplikatom powiadomień
            prefs.lastCheckedEventIds = allEvents.map { it.id }.toSet()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Blad podczas sprawdzania zdarzen", e)
            Result.retry() // W przypadku błędu sieci, WorkManager spróbuje ponownie później
        }
    }

    /**
     * Sprawdza, czy punkt geograficzny zdarzenia znajduje się w zadanym promieniu (radiusKm) od pozycji użytkownika.
     */
    private fun isEventInRadius(event: Event, userLat: Double, userLng: Double, radiusKm: Double): Boolean {
        if (event.geometries.isEmpty()) return false
        val geo = event.geometries.first()
        val coords = geo.coordinates
        try {
            // Przypadek 1: Standardowe współrzędne [longitude, latitude]
            if (coords.size() >= 2 && coords[0].isJsonPrimitive) {
                val eventLng = coords[0].asDouble
                val eventLat = coords[1].asDouble
                return haversineDistance(userLat, userLng, eventLat, eventLng) <= radiusKm
            } else {
                // Przypadek 2: Zagnieżdżona struktura współrzędnych wielokąta
                var current = coords
                while (current.size() > 0 && current[0].isJsonArray) {
                    current = current[0].asJsonArray
                }
                if (current.size() >= 2 && current[0].isJsonPrimitive) {
                    val eventLng = current[0].asDouble
                    val eventLat = current[1].asDouble
                    return haversineDistance(userLat, userLng, eventLat, eventLng) <= radiusKm
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Nie udalo sie sparsowac wspolrzednych dla ${event.id}", e)
        }
        return false
    }

    /**
     * Oblicza odległość w kilometrach między dwoma punktami (lat/lng) przy użyciu formuły Haversine.
     */
    private fun haversineDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Przygotowuje i wysyła systemowe powiadomienie push z podsumowaniem nowych zdarzeń.
     */
    private fun sendNotification(events: List<Event>) {
        createNotificationChannel()

        val count = events.size
        val firstEvent = events.first()
        val catTitle = if (firstEvent.categories.isNotEmpty())
            translateCategory(firstEvent.categories.first().title) else "Zdarzenie"

        // Dynamiczny tytuł i treść w zależności od liczby nowych zdarzeń
        val title = if (count == 1) "Nowe zdarzenie naturalne!"
            else "Nowe zdarzenia naturalne! ($count)"

        val text = if (count == 1) "$catTitle: ${firstEvent.title}"
            else "${firstEvent.title} i ${count - 1} wiecej"

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Sprawdzenie wymaganego uprawnienia dla Androida 13+ przed wysłaniem powiadomienia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    /**
     * Tworzy kanał powiadomień (Notification Channel) wymagany na systemach Android 8.0+.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm zdarzen naturalnych",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o nowych zdarzeniach naturalnych"
            }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

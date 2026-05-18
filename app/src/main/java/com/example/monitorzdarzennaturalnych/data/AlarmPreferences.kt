package com.example.monitorzdarzennaturalnych.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Przechowuje ustawienia alarmu użytkownika w SharedPreferences.
 * - alarmEnabled: czy alarm jest aktywny
 * - radiusKm: zasięg monitorowania w km (0 = cała planeta)
 * - userLat / userLng: lokalizacja centrum monitorowania (domyślnie 0,0 = cała planeta)
 * - lastCheckedEventIds: zbiór ID zdarzeń, które już były zgłoszone
 */
class AlarmPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ALARM_ENABLED = "alarm_enabled"
        private const val KEY_RADIUS_KM = "radius_km"
        private const val KEY_USER_LAT = "user_lat"
        private const val KEY_USER_LNG = "user_lng"
        private const val KEY_LAST_CHECKED_IDS = "last_checked_ids"
    }

    var alarmEnabled: Boolean
        get() = prefs.getBoolean(KEY_ALARM_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ALARM_ENABLED, value).apply()

    var radiusKm: Int
        get() = prefs.getInt(KEY_RADIUS_KM, 0) // 0 = cała planeta
        set(value) = prefs.edit().putInt(KEY_RADIUS_KM, value).apply()

    var userLat: Double
        get() = Double.fromBits(prefs.getLong(KEY_USER_LAT, 0.0.toBits()))
        set(value) = prefs.edit().putLong(KEY_USER_LAT, value.toBits()).apply()

    var userLng: Double
        get() = Double.fromBits(prefs.getLong(KEY_USER_LNG, 0.0.toBits()))
        set(value) = prefs.edit().putLong(KEY_USER_LNG, value.toBits()).apply()

    var lastCheckedEventIds: Set<String>
        get() = prefs.getStringSet(KEY_LAST_CHECKED_IDS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_LAST_CHECKED_IDS, value).apply()

    /** Czyści cały alarm (wyłącza i resetuje ustawienia) */
    fun clearAlarm() {
        prefs.edit()
            .putBoolean(KEY_ALARM_ENABLED, false)
            .putInt(KEY_RADIUS_KM, 0)
            .apply()
    }
}

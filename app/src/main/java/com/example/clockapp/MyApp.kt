package com.example.clockapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import org.threeten.bp.zone.ZoneRulesProvider
import java.util.TimeZone

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // for use localtime instead  of java time.localtime in lower api levels
//        AndroidThreeTen.init(this)// This initializes the timezone data

        AndroidThreeTen.init(this)
    }
}

//private fun initThreeTenBP() {
//    try {
//        ZoneRulesProvider.getAvailableZoneIds() // Force initialization
//    } catch (e: Exception) {
//        // Fallback if initialization fails
//        val tz = TimeZone.getDefault()
//        org.threeten.bp.ZoneId.of(tz.id)
//    }
//}

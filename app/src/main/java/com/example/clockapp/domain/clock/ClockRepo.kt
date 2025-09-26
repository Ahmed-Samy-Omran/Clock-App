package com.example.clockapp.domain.clock

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

interface ClockRepo {

    // A stream that emits LocalTime every second for the provided ZoneId.
    fun timeFlow(zoneId: ZoneId = ZoneId.systemDefault()): Flow<LocalTime>

    suspend fun getRegionFromLocation(latitude:Double, longitude:Double):String

    //Return the ZoneId string for the device (or chosen region).
    suspend fun getZoneId(): ZoneId

    suspend fun  getCurrentCoordinates() :Pair<Double, Double>?
    suspend fun  getCoordinatesFromSystemLocationManager() :Pair<Double, Double>?

    // Return region automatically using requestLocationUpdates
    suspend fun getRegionAuto(onResult: (String) -> Unit)

    suspend fun getRegionWithFallback():String
    suspend fun getRegionFromTimezone():String

}
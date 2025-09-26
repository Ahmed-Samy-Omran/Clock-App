package com.example.clockapp.data.clock


import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import com.example.clockapp.domain.clock.ClockRepo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Suppress("DEPRECATION")
@Singleton
class ClockRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ClockRepo {

    //Emits the current time every second based on the given ZoneId.
    override fun timeFlow(zoneId: ZoneId): Flow<LocalTime> = flow {
        while (currentCoroutineContext().isActive) {
            emit(LocalTime.now(zoneId))
            delay(1000L)
        }
    }.flowOn(Dispatchers.Default)

    //  Returns the system default ZoneId of phone (timezone).
    override suspend fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }

//     Tries to get current coordinates (latitude, longitude).
//     First uses Google Play Services, then falls back to Android's LocationManager.
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentCoordinates(): Pair<Double, Double>? {
        return try {
            // Try Google Play Services first
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                suspendCancellableCoroutine { cont ->
                    fusedClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            cont.resume(Pair(location.latitude, location.longitude))
                        } else {
                            cont.resume(null)
                        }
                    }.addOnFailureListener {
                        cont.resume(null)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.d("LocationDebug", "Google Play Services failed, trying system location")
                // Fallback to Android System Location Manager
                getCoordinatesFromSystemLocationManager()
            }
        } catch (e: Exception) {
            null
        }
    }

    // fallback method if Google Play Services is not working.
    @SuppressLint("MissingPermission")
    override suspend fun getCoordinatesFromSystemLocationManager(): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Try GPS provider
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (gpsLocation != null) {
                cont.resume(Pair(gpsLocation.latitude, gpsLocation.longitude))
                return@suspendCancellableCoroutine
            }

            // Try network provider
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (networkLocation != null) {
                cont.resume(Pair(networkLocation.latitude, networkLocation.longitude))
                return@suspendCancellableCoroutine
            }

            cont.resume(null)
        }
    }


    // here convert lat, lng to region name using Geocoder
    @SuppressLint("MissingPermission")
    override suspend fun getRegionFromLocation(
        latitude: Double,
        longitude: Double
    ): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // بناء العنوان بالتفصيل + سطر جديد بين كل جزء
                val parts = listOfNotNull(
                    address.adminArea,      // Governorate (e.g., Cairo Governorate)
                    address.locality,       // City (e.g., Cairo)
                    address.countryName     // Country (e.g., Egypt)
                )

                return@withContext parts.joinToString(",\n") // هنا عملنا سطر جديد بين كل جزء
            } else {
                return@withContext "Unknown location"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Unknown location"
        }
    }


//     Automatically updates region by listening to location updates.
//     Calls onResult with the region name when location is received.
    @SuppressLint("MissingPermission")
    override suspend fun getRegionAuto(onResult: (String) -> Unit) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address = addresses?.firstOrNull()

                        val region = listOfNotNull(
                            address?.subLocality,   // القرية/الحي
                            address?.locality,      // المدينة
                            address?.countryName    // الدولة
                        ).joinToString(", ")

                        onResult(region)
                        fusedClient.removeLocationUpdates(this)
                    } catch (e: Exception) {
                        onResult("Unknown Location")
                    }
                } else {
                    onResult("Unknown Location")
                }
            }
        }

        fusedClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            android.os.Looper.getMainLooper()
        )
    }

   // Gets region using GPS if available, otherwise falls back to timezone-based region.
    override suspend fun getRegionWithFallback(): String {
        return try {
            // Try GPS location first
            val coords = getCurrentCoordinates()
            if (coords != null) {
                val (lat, lng) = coords
                getRegionFromLocation(lat, lng)
            } else {
                // Fallback to timezone-based region
                getRegionFromTimezone()
            }
        } catch (e: Exception) {
            getRegionFromTimezone()
        }
    }


    //The last fallback returns the name of the location from the device's TimeZone.
    override suspend fun getRegionFromTimezone(): String {
        val timezone = TimeZone.getDefault()
        val zoneId = timezone.id
        return zoneId.substringAfterLast("/", zoneId).replace("_", " ")
    }
}


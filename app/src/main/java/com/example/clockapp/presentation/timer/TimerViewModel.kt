package com.example.clockapp.presentation.timer

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.domain.clock.ClockRepo
import com.example.clockapp.utils.formatDate
import com.example.clockapp.utils.formatFullDate
import com.example.clockapp.utils.formatHour
import com.example.clockapp.utils.formatMinute
import com.example.clockapp.utils.formatSecond
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

data class ClockUiState(
    val hour: String = "__",
    val minute: String = "__",
    val second: String = "__",
    val amPm: String = "AM",
    val is24hFormat: Boolean = true,   // ✅ Boolean instead of String
    val date: String = "",
    val region: String = "Loading..."
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repo: ClockRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClockUiState())
    val uiState: StateFlow<ClockUiState> = _uiState

    /** ✅ Update region */
    fun updateRegion(newRegion: String) {
        _uiState.update { it.copy(region = newRegion) }
    }

    /** ✅ Toggle format */
    fun toggleTimeFormat(is24h: Boolean) {
        _uiState.update { it.copy(is24hFormat = is24h) }
    }

    init {
        // 1) تحميل المنطقة تلقائي
        loadRegionAutomatically()

        // 2) متابعة الوقت كل ثانية
        viewModelScope.launch {
            val zone = repo.getZoneId()
            repo.timeFlow(zone).collect { localTime ->
                val is24h = _uiState.value.is24hFormat

                val hour = formatHour(localTime, is24hFormat = is24h)
                val minute = formatMinute(localTime)
                val second = formatSecond(localTime)
                val date = formatFullDate(LocalDate.now())

                val amPm = if (localTime.hour < 12) "AM" else "PM"

                _uiState.update {
                    it.copy(
                        hour = hour,
                        minute = minute,
                        second = second,
                        date = date,
                        amPm = amPm
                    )
                }
            }
        }
    }

    /** ✅ تحميل المنطقة تلقائي */
    fun loadRegionAutomatically() {
        viewModelScope.launch {
            try {
                // 1- أول حاجة loading
                updateRegion("Loading region, please wait...")

                // هنا ممكن تعرض ProgressBar أو Animation في UI بناءً على state
                delay(2000) // وقت بسيط عشان يبان loading في الفيديو

                // 2- بعد كده المكان اللي عايزه يظهر في الفيديو
                updateRegion("Egypt , Qalyubia ,Shubra Haris")

                // 🎥 بعد ما تخلص تسجيل الفيديو..
                // رجّع الكود القديم اللي بيجيب المكان الحقيقي من الـ repo

                /*
                val coords = repo.getCurrentCoordinates()
                if (coords != null) {
                    val (lat, lng) = coords
                    val regionName = repo.getRegionFromLocation(lat, lng)
                    updateRegion(regionName)
                } else {
                    repo.getRegionAuto { regionName ->
                        updateRegion(regionName)
                    }
                }
                */
            } catch (e: Exception) {
                val fallback = repo.getRegionFromTimezone()
                updateRegion(fallback)
            }
        }
    }
}
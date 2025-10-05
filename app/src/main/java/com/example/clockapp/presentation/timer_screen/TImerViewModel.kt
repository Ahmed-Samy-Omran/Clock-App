package com.example.clockapp.presentation.timer_screen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.domain.timer.TimerRepo
import com.example.clockapp.presentation.components.TimerReceiver
import com.example.clockapp.presentation.components.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max


// Represents the state of the timer (UI state)
data class TimerUiState(
    val lastTotalTime: Long = 0L,
    val remainingTime: Long = 0L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val showProgressArc: Boolean = false
)

//@HiltViewModel
//class TImerViewModel @Inject constructor(
//    private val timerRepo: TimerRepo,
//    private val savedStateHandle: SavedStateHandle // Save state when app killed or rotated
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(TimerUiState())
//    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()
//
//    private var tickerJop: Job? = null // Track timer coroutine job
//    private var endTimes: Long = 0L    // Time when timer should end
//
//    init {
//        // Load last saved total time from DataStore
//        viewModelScope.launch {
//            val lastTime = timerRepo.getLastTotalTime().first()
//            if (lastTime > 0L) {
//                _uiState.update { it.copy(lastTotalTime = lastTime, remainingTime = lastTime) }
//            }
//        }
//    }
//
//    fun reset(context: Context) {
//        tickerJop?.cancel()
//        cancelAlarm(context)
//
//        // Stop TimerService if running
//        try {
//            val serviceIntent = Intent(context, TimerService::class.java)
//            context.stopService(serviceIntent)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        // Reset UI state
//        _uiState.update { TimerUiState() }
//    }
//
//    fun startTimer(totalTime: Long, context: Context) {
//        if (totalTime <= 0L) return
//        tickerJop?.cancel()
//
//        val now = SystemClock.elapsedRealtime()
//        endTimes = now + totalTime
//
//        // Update UI to show timer started
//        _uiState.update {
//            it.copy(
//                isFinished = false,
//                isRunning = true,
//                lastTotalTime = totalTime,
//                remainingTime = totalTime,
//                showProgressArc = true
//            )
//        }
//
//        // Save last total time to DataStore
//        viewModelScope.launch { timerRepo.saveLastTotalTime(totalTime) }
//
//        // Schedule system alarm
//        scheduleAlarm(context, totalTime)
//
//        // Start countdown coroutine
//        startTicker()
//    }
//    // define alarmManager that run BroadCast when timer is finished
//    private fun scheduleAlarm(context: Context, remainingTimeMillis: Long) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, TimerReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val triggerTime = System.currentTimeMillis() + remainingTimeMillis
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            triggerTime,
//            pendingIntent
//        )
//    }
//
//    fun pause() {
//        if (!_uiState.value.isRunning) return
//        tickerJop?.cancel()
//
//        val now = SystemClock.elapsedRealtime()
//        val remaining = max(0L, endTimes - now) // Ensure no negative value
//
//        // Update UI to paused state
//        _uiState.update {
//            it.copy(
//                isRunning = false,
//                remainingTime = remaining,
//                showProgressArc = true
//            )
//        }
//    }
//
//    fun resume() {
//        val remainingTime = _uiState.value.remainingTime
//        if (remainingTime <= 0L) return
//
//        val now = SystemClock.elapsedRealtime()
//        endTimes = now + remainingTime
//
//        // Update UI to resume running
//        _uiState.update {
//            it.copy(
//                isRunning = true,
//                isFinished = false,
//                showProgressArc = true
//            )
//        }
//        startTicker()
//    }
//
//    private fun cancelAlarm(context: Context) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, TimerReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        alarmManager.cancel(pendingIntent)
//    }
//
//    private fun startTicker() {
//        tickerJop?.cancel() // Cancel previous ticker
//        tickerJop = viewModelScope.launch {
//            while (true) {
//                val now = SystemClock.elapsedRealtime()
//                val rem = max(0L, endTimes - now)
//
//                // Update remaining time
//                _uiState.update { it.copy(remainingTime = rem) }
//
//                if (rem <= 0L) {
//                    // Timer finished
//                    _uiState.update {
//                        it.copy(
//                            isRunning = false,
//                            isFinished = true,
//                            remainingTime = 0L
//                        )
//                    }
//                    break // Stop loop
//                }
//
//                delay(200L) // Prevent high CPU usage
//            }
//        }
//    }
//
//    override fun onCleared() {
//        tickerJop?.cancel() // Cancel job when ViewModel destroyed
//        super.onCleared()
//    }
//}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepo: TimerRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // CONSTANTS
    private companion object {
        private const val TICK_INTERVAL = 200L
        private const val ALARM_REQUEST_CODE = 0
    }

    // STATE
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var endTime: Long = 0L

    // Expose derived state for UI (instead of calculating in Composable)
    val canStart: StateFlow<Boolean> = _uiState.map { state ->
        (state.remainingTime > 0L && !state.isFinished) || state.lastTotalTime > 0L
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val canReset: StateFlow<Boolean> = _uiState.map { state ->
        state.isRunning || state.remainingTime > 0L || state.lastTotalTime > 0L
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)


    init {
        viewModelScope.launch {
            val lastTime = timerRepo.getLastTotalTime().first()
            if (lastTime > 0L) {
                _uiState.update { it.copy(lastTotalTime = lastTime, remainingTime = lastTime) }
            }
        }
    }



    // Reset timer and cancel alarms/services
    fun reset(context: Context) {
        tickerJob?.cancel()
        cancelAlarm(context)

        try {
            val serviceIntent = Intent(context, TimerService::class.java)
            context.stopService(serviceIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _uiState.update { TimerUiState() }
    }

//     Start timer with total duration
    fun startTimer(totalTime: Long, context: Context) {
        if (totalTime <= 0L) return
        tickerJob?.cancel()

        val now = SystemClock.elapsedRealtime()
        endTime = now + totalTime

        _uiState.update {
            it.copy(
                isFinished = false,
                isRunning = true,
                lastTotalTime = totalTime,
                remainingTime = totalTime,
                showProgressArc = true
            )
        }

        viewModelScope.launch {
            timerRepo.saveLastTotalTime(totalTime)
        }

        scheduleAlarm(context, totalTime)
        startTicker()
    }

//    Pause timer but keep remaining time
    fun pause() {
        if (!_uiState.value.isRunning) return
        tickerJob?.cancel()

        val now = SystemClock.elapsedRealtime()
        val remaining = max(0L, endTime - now)

        _uiState.update {
            it.copy(isRunning = false, remainingTime = remaining, showProgressArc = true)
        }
    }

  // Resume timer from saved remaining time
    fun resume() {
        val remainingTime = _uiState.value.remainingTime
        if (remainingTime <= 0L) return

        val now = SystemClock.elapsedRealtime()
        endTime = now + remainingTime

        _uiState.update {
            it.copy(isRunning = true, isFinished = false, showProgressArc = true)
        }

        startTicker()
    }

    //  HELPERS
    private fun scheduleAlarm(context: Context, remainingTimeMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + remainingTimeMillis
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val rem = max(0L, endTime - now)

                _uiState.update { it.copy(remainingTime = rem) }

                if (rem <= 0L) {
                    _uiState.update {
                        it.copy(isRunning = false, isFinished = true, remainingTime = 0L)
                    }
                    break
                }

                delay(TICK_INTERVAL)
            }
        }
    }

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}

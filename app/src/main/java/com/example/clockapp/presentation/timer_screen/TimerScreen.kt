package com.example.clockapp.presentation.timer_screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.clockapp.R
import com.example.clockapp.presentation.components.CircleIconButton
import com.example.clockapp.presentation.components.NumberColumn
import com.example.clockapp.presentation.components.TimerClock
import com.example.clockapp.utils.formatTimerDuration
import kotlinx.coroutines.launch



@Composable
fun TimerScreen(
    navHostController: NavHostController? = null,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // picked values
    var hours by rememberSaveable { mutableStateOf(0) }
    var minutes by rememberSaveable { mutableStateOf(0) }
    var seconds by rememberSaveable { mutableStateOf(0) }

    // show/hide pickers
    var showPicker by rememberSaveable { mutableStateOf(true) }
    // show/hide progress arc (blue border)
    var showProgressArc by rememberSaveable { mutableStateOf(false) }

    // states for each LazyColumn so we can control scrolling (reset -> scroll to 0)
    val hoursState = rememberLazyListState(initialFirstVisibleItemIndex = hours.coerceAtLeast(0))
    val minutesState = rememberLazyListState(initialFirstVisibleItemIndex = minutes.coerceAtLeast(0))
    val secondsState = rememberLazyListState(initialFirstVisibleItemIndex = seconds.coerceAtLeast(0))

    val coroutineScope = rememberCoroutineScope()

    // context + notification prefs
    val context = LocalContext.current
    val notificationPrefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)


    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // استخدام الطريقة الآمنة حسب نسخة الـ Android
            val pickedUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as? Uri
            }

            // احفظ الـ URI لو مش null
            pickedUri?.let { uri ->
                notificationPrefs.edit().putString("notification_sound", uri.toString()).apply()
            }
        }
    }



    // helpers
    val pickedTotalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
    val isTimeSelected = (hours + minutes + seconds) > 0
    val canStart = isTimeSelected || (uiState.remainingTime > 0L && !uiState.isFinished)
    val canReset = uiState.isRunning || uiState.remainingTime > 0L || isTimeSelected



     // Always show TimerClock (the clock itself)
        val clockTotal =
            if (uiState.lastTotalTime > 0L) uiState.lastTotalTime else pickedTotalMillis
        val clockRemaining =
            if (uiState.isRunning || uiState.remainingTime > 0L) uiState.remainingTime else pickedTotalMillis


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 28.dp, end = 28.dp),
    ) {
        Text(
            text = "Timer",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Clock section with fixed height
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(280.dp), // Fixed height
            contentAlignment = Alignment.Center
        ) {
            TimerClock(
                totalTimeMillis = clockTotal,
                remainingTimeMillis = clockRemaining,
                modifier = Modifier,
                isRunning = uiState.isRunning
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Time display text
        Text(
            text = formatTimerDuration(clockRemaining),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Pickers (only show when showPicker == true)
        if (showPicker) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hours
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    NumberColumn(
                        range = 0..23,
                        state = hoursState,
                        initialValue = hours,
                        onValueChange = { hours = it },
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = "h",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }

                Text(
                    ":",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)

                )

                // Minutes
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    NumberColumn(
                        range = 0..59,
                        state = minutesState,
                        initialValue = minutes,
                        onValueChange = { minutes = it },
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = "min",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }

                Text(
                    ":",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                )

                // Seconds
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    NumberColumn(
                        range = 0..59,
                        state = secondsState,
                        initialValue = seconds,
                        onValueChange = { seconds = it },
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = "sec",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(8.dp))

        // Controls: Reset / Play-Pause / Notification
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Fixed height for buttons row
        ) {
            // Reset button
            CircleIconButton(
                icon = Icons.Default.Refresh,
                backgroundColor = Color(0xFFF2F2F2),
                iconColor = Color.Black,
                borderColor = Color(0x22000000),
                size = 56.dp,
                animated = true,
                onClick = {
                    viewModel.reset(context)
                    hours = 0; minutes = 0; seconds = 0
                    showPicker = true
                    showProgressArc = false
                    coroutineScope.launch {
                        hoursState.animateScrollToItem(0)
                        minutesState.animateScrollToItem(0)
                        secondsState.animateScrollToItem(0)
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Play / Pause button
            CircleIconButton(
                painter = painterResource(
                    if (uiState.isRunning) R.drawable.stop_ic else R.drawable.play_ic
                ),
                backgroundColor = if (canStart) Color.Black else Color(0xFFEEEEEE),
                iconColor = if (canStart) Color.White else Color.Gray,
                size = 72.dp,
                animated = true,
                onClick = {
                    if (canStart) {
                        if (uiState.isRunning) {
                            viewModel.pause()
                            showProgressArc = true
                        } else {
                            if (uiState.remainingTime > 0L && !uiState.isFinished) {
                                viewModel.resume()
                            } else {
                                viewModel.startTimer(pickedTotalMillis, context)
                            }
                            showPicker = false
                            showProgressArc = true
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Notification picker button
            CircleIconButton(
                icon = Icons.Default.Notifications,
                backgroundColor = Color.Transparent,
                iconColor = Color.Black,
                borderColor = Color(0x22000000),
                size = 56.dp,
                animated = false,
                onClick = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    }
                    ringtonePickerLauncher.launch(intent)
                }
            )
        }
    }
}


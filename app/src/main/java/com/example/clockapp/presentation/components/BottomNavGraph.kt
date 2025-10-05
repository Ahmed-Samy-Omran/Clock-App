package com.example.clockapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clockapp.presentation.clock.ClockScreen
import com.example.clockapp.presentation.location_alarm_screen.LocationAlarmScreen
import com.example.clockapp.presentation.timer_screen.TimerScreen
import com.example.clockapp.presentation.worldclock.WorldClockScreen

@Composable
fun BottomNavGraph(navController: NavHostController,
                   modifier: Modifier = Modifier
                   ){
    NavHost(navController = navController, startDestination = Screen.ClockScreen.route) {
        composable(Screen.LocationAlarmScreen.route) { LocationAlarmScreen(navController) }
        composable(Screen.TimerScreen.route) { TimerScreen(navController) }
        composable(Screen.WorldClockScreen.route) { WorldClockScreen(navController) }
        composable(Screen.ClockScreen.route) { ClockScreen(navController) }

    }
}


package com.example.clockapp.presentation.components

sealed class Screen(val route: String)  {
    object TimerScreen : Screen("timer_screen")
    object WorldClockScreen : Screen("clock_screen")
    object StopWatchScreen : Screen("stopwatch_screen")
    object LocationAlarmScreen : Screen("location_alarm_screen")
}
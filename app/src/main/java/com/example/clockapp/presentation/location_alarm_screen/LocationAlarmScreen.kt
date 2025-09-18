package com.example.clockapp.presentation.location_alarm_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LocationAlarmScreen(
    navController: NavHostController,
) {

    Box(modifier =Modifier.size(36.dp),

    ){
        Text(text = "Location Alarm", fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
    }




}
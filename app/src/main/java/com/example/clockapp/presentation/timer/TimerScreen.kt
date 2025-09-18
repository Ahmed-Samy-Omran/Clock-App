package com.example.clockapp.presentation.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun TimerScreen(
    navHostController: NavHostController,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(), // fill the whole screen
        contentAlignment = Alignment.Center // center content inside
    ) {
        Text(
            text = "Timer",
            color = Color.Yellow,
            fontSize = 20.sp // bigger font so it’s visible
        )
    }
}
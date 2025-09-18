package com.example.clockapp.presentation.worldclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
fun WorldClockScreen(
    navController: NavHostController,
) {
    Box(modifier = Modifier.size(36.dp),

        ){
        Text(text = "World Clock", fontSize = 30.sp, modifier = Modifier.align(Alignment.Center).background(color = Color.Yellow)

        )
    }

}
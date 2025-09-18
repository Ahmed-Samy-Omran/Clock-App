package com.example.clockapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.clockapp.presentation.components.BottomNavGraph
import com.example.clockapp.presentation.components.CustomBottomNav


@Composable
fun ClockApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            CustomBottomNav(navController = navController)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
                .padding(innerPadding)
        ) {
            BottomNavGraph(
                navController = navController)

        }

    }

}
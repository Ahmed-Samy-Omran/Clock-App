package com.example.clockapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.clockapp.R


// Each item in the bottom navigation (screen + icon + label)
data class BottomNavItem(
    val screen: Screen,
    val icon: Int,
    val label: String
)

@Composable
fun CustomBottomNav(navController: NavController) {

    // list of items in navigation
    val items = listOf(
        BottomNavItem(Screen.ClockScreen, R.drawable.alarm_ic, "Clock"),
        BottomNavItem(Screen.WorldClockScreen, R.drawable.world_ic, "World Clock"),
        BottomNavItem(Screen.TimerScreen, R.drawable.stopwatch_ic, "Timer"),
        BottomNavItem(Screen.LocationAlarmScreen, R.drawable.location_ic, "Location Alarm"),
    )

    // get the current route (screen user is currently in)
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // wrap all in box to make it center with padding from bottom nad arround item
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            // loop through all items
            items.forEach { item ->

                // check if the item is selected (current route matches item's route)
                val isSelected = currentRoute == item.screen.route

                // Box = circular background for each icon
                Box(
                    modifier = Modifier
                        .size(60.dp) // circle size
                        .clip(CircleShape)
                        .background(if (isSelected) Color.Black else Color.White)

                        .clickable {
                            if (!isSelected) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.TimerScreen.route) // clear back stack to first screen
                                    launchSingleTop =
                                        true // avoid multiple copies of same destination
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,

                    ) {
                    // the icon itself inside the circle
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

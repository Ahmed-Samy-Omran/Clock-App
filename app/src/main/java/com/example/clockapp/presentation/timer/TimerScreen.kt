package com.example.clockapp.presentation.timer

import ClockTicker
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import android.Manifest
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.example.clockapp.presentation.components.TimeFormatSegmentedControl
import com.example.clockapp.ui.theme.ClockAppTheme

@Composable
fun TimerScreen(
    navHostController: NavHostController,
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by timerViewModel.uiState.collectAsState()
    val context = LocalContext.current

    //Prepares a launcher to request permission from the user.
// inCase it granted it will load region automatically else it will show permission denied
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            timerViewModel.loadRegionAutomatically()
        } else {
            timerViewModel.updateRegion("Permission denied")
        }
    }

    //  if permission is already granted:
    //If yes → loads region automatically.
    //If not → requests permission from the use
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, locationPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            timerViewModel.loadRegionAutomatically()
        } else {
            launcher.launch(locationPermission)
        }
    }

    TimerContent(
        hour = uiState.hour,
        date = uiState.date,
        minute = uiState.minute,
        seconds = uiState.second,
        region = uiState.region,
        amPm = uiState.amPm,
        uiState = uiState,
        onFormatChange = { is24h -> timerViewModel.toggleTimeFormat(is24h) }
    )
}


@Composable
fun TimerContent(
    hour: String,
    date: String,
    minute: String,
    seconds: String,
    region: String,
    amPm: String,
    uiState: ClockUiState,
    onFormatChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 28.dp, end = 28.dp)


    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Start",
                color = Color.Black,
                fontSize = 26.sp,
            )

            // responsible for changing the time format by SegmentedButton .
            TimeFormatSegmentedControl(
                onFormatChange = onFormatChange,
                is24h = uiState.is24hFormat
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,


            ) {

            ClockTicker(
                value = hour,
                fontSize = 110,
                fontWeight = FontWeight.Bold,

                )


            Spacer(modifier = Modifier.width(31.dp))


            ClockTicker(
                value = date, 30, modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Normal,
                lineHeight = 40.sp
            )
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp),
            horizontalArrangement = Arrangement.Start
        ) {


            ClockTicker(
                value = minute,
                fontSize = 110,
                fontWeight = FontWeight.Black,
            )

            Spacer(modifier = Modifier.width(31.dp))


            ClockTicker(
                value = seconds, 30, modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.width(10.dp))

            if(!uiState.is24hFormat){
                ClockTicker(
                    value = amPm, 30, modifier = Modifier
                        .align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Normal
                )
            }

        }

        Spacer(modifier = Modifier.height(15.dp))

        // it checks if the region contains loading or Getting return true else false
        val isLoadingRegion = region.contains("Loading") || region.contains("Getting")

        if (isLoadingRegion) {
            // the case of loading region
            ClockTicker(
                value = region,
                fontSize = 24,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )
        } else {
            // the case of showing region
            val regionParts = region.split(",")

            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                regionParts.forEach { part ->
                    ClockTicker(
                        value = part.trim(),
                        fontSize = 40,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 52.sp

                    )
                }
            }
        }
    }
}

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//
//fun TimerScreenPreview() {
//    ClockAppTheme {
//        TimerContent(
//            hour = "08",
//            date = "Thu, 20 Mar",
//            minute = "40",
//            seconds = "15",
//            region = "Egypt , Qalyubia ,Shubra Haris",
//            amPm = "AM",
//            uiState = ClockUiState(),
//            onFormatChange = {}
//        )
//    }
//}

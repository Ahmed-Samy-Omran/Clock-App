package com.example.clockapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TimeFormatSegmentedControl(
    is24h: Boolean, // current selected format (true = 24h, false = 12h)
    onFormatChange: (Boolean) -> Unit, // callback when user changes format
    modifier: Modifier = Modifier
) {

    val selectedIndex = if (is24h) 1 else 0


    // SingleChoiceSegmentedButtonRow :that is the container for the segmented buttons
    SingleChoiceSegmentedButtonRow {
        // first button
        SegmentedButton(
            selected = selectedIndex == 0,
            onClick = { onFormatChange(false) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2), // shape for left item
            icon = {},
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = Color.Black,   // خلفية الـ selected
                activeContentColor = Color.White,     // لون النص للـ selected
                inactiveContainerColor = Color.Transparent, // خلفية العادي
                inactiveContentColor = Color.Gray     // لون النص للعادي
            ),
            // that for define size of button(left)
            modifier = Modifier
                .height(35.dp)
                .width(60.dp),

        ) {
            Text(
                "12h"
            )
        }

        SegmentedButton(
            // second button (right)
            selected = selectedIndex == 1,
            onClick = { onFormatChange(true) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            icon = {},
            modifier = Modifier
                .height(35.dp)
                .width(60.dp),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = Color.Black,
                activeContentColor = Color.White,
                inactiveContainerColor = Color.Transparent,
                inactiveContentColor = Color.Gray
            ),


            ) {
            Text("24h")
        }
    }
}



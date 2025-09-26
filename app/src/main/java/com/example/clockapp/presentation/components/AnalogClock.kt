package com.example.clockapp.presentation.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun AnalogClock(
    hours: Int,
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier.size(350.dp)
        .padding( vertical = 26.dp)
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Draw ticks + numbers
        for (i in 0 until 60) {
            val angle = Math.toRadians((i * 6).toDouble() - 90)
            val lineLength = if (i % 5 == 0) 30f else 15f
            val start = Offset(
                (center.x + (radius - lineLength) * cos(angle)).toFloat(),
                (center.y + (radius - lineLength) * sin(angle)).toFloat()
            )
            val end = Offset(
                (center.x + radius * cos(angle)).toFloat(),
                (center.y + radius * sin(angle)).toFloat()
            )
            drawLine(Color.Gray, start, end, strokeWidth = 3f)

            // Draw numbers (for every 5 ticks)
            if (i % 5 == 0) {
                val number = if (i == 0) 12 else i / 5

                // 👇 زودنا المسافة بين الرقم والخط
                val textRadius = radius - 70f   // بدل 50f → خليتها 70f عشان يبعد أكتر عن الخط

                val textX = (center.x + textRadius * cos(angle)).toFloat()
                val textY = (center.y + textRadius * sin(angle)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        number.toString(),
                        textX,
                        textY + 12, // ظبط خفيف للنزول تحت
                        Paint().apply {
                            textSize = 40f   // 👈 أصغر (كان 40f)
                            textAlign = Paint.Align.CENTER
                            color = android.graphics.Color.BLACK
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }

        val hourAngle = Math.toRadians(((hours % 12 + minutes / 60f) * 30 - 90).toDouble())
        drawLine(
            Color.Black,
            center,
            Offset(
                (center.x + radius * 0.6f * cos(hourAngle)).toFloat(), // بدل 0.5 خلتها 0.6
                (center.y + radius * 0.6f * sin(hourAngle)).toFloat()
            ),
            strokeWidth = 12f,
            cap = StrokeCap.Round
        )

// Minute hand
        val minuteAngle = Math.toRadians((minutes * 6 - 90).toDouble())
        drawLine(
            Color.DarkGray,
            center,
            Offset(
                (center.x + radius * 0.8f * cos(minuteAngle)).toFloat(), // بدل 0.7 → 0.8
                (center.y + radius * 0.8f * sin(minuteAngle)).toFloat()
            ),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )

// Second hand (usually almost full length)
        val secondAngle = Math.toRadians((seconds * 6 - 90).toDouble())
        drawLine(
            Color.Red,
            center,
            Offset(
                (center.x + radius * 0.95f * cos(secondAngle)).toFloat(), // بدل 0.9 → 0.95
                (center.y + radius * 0.95f * sin(secondAngle)).toFloat()
            ),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}
//@Composable
//fun AnalogClock(
//    modifier: Modifier = Modifier.size(300.dp)
//) {
//    // الحالة اللي هتمسك الوقت الحالي
//    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
//
//    // كل ثانية نعمل update للوقت
//    LaunchedEffect(Unit) {
//        while (true) {
//            calendar = Calendar.getInstance()
//            delay(1000L) // كل ثانية
//        }
//    }
//
//    val hours = calendar.get(Calendar.HOUR)
//    val minutes = calendar.get(Calendar.MINUTE)
//    val seconds = calendar.get(Calendar.SECOND)
//
//    Canvas(modifier = modifier) {
//        val radius = size.minDimension / 2
//        val center = Offset(size.width / 2, size.height / 2)
//
//        // Draw ticks + numbers
//        for (i in 0 until 60) {
//            val angle = Math.toRadians((i * 6).toDouble() - 90)
//            val lineLength = if (i % 5 == 0) 30f else 15f
//            val start = Offset(
//                (center.x + (radius - lineLength) * cos(angle)).toFloat(),
//                (center.y + (radius - lineLength) * sin(angle)).toFloat()
//            )
//            val end = Offset(
//                (center.x + radius * cos(angle)).toFloat(),
//                (center.y + radius * sin(angle)).toFloat()
//            )
//            drawLine(Color.Gray, start, end, strokeWidth = 3f)
//
//            // Draw numbers (for every 5 ticks)
//            if (i % 5 == 0) {
//                val number = if (i == 0) 12 else i / 5
//                val textRadius = radius - 50f
//                val textX = (center.x + textRadius * cos(angle)).toFloat()
//                val textY = (center.y + textRadius * sin(angle)).toFloat()
//
//                drawContext.canvas.nativeCanvas.apply {
//                    drawText(
//                        number.toString(),
//                        textX,
//                        textY + 15,
//                        Paint().apply {
//                            textSize = 40f
//                            textAlign = Paint.Align.CENTER
//                            color = android.graphics.Color.BLACK
//                            isFakeBoldText = true
//                        }
//                    )
//                }
//            }
//        }
//
//        // Hour hand
//        val hourAngle = Math.toRadians(((hours + minutes / 60f) * 30 - 90).toDouble())
//        drawLine(
//            Color.Black,
//            center,
//            Offset(
//                (center.x + radius * 0.5f * cos(hourAngle)).toFloat(),
//                (center.y + radius * 0.5f * sin(hourAngle)).toFloat()
//            ),
//            strokeWidth = 12f,
//            cap = StrokeCap.Round
//        )
//
//        // Minute hand
//        val minuteAngle = Math.toRadians((minutes * 6 - 90).toDouble())
//        drawLine(
//            Color.DarkGray,
//            center,
//            Offset(
//                (center.x + radius * 0.7f * cos(minuteAngle)).toFloat(),
//                (center.y + radius * 0.7f * sin(minuteAngle)).toFloat()
//            ),
//            strokeWidth = 8f,
//            cap = StrokeCap.Round
//        )
//
//        // Second hand
//        val secondAngle = Math.toRadians((seconds * 6 - 90).toDouble())
//        drawLine(
//            Color.Red,
//            center,
//            Offset(
//                (center.x + radius * 0.9f * cos(secondAngle)).toFloat(),
//                (center.y + radius * 0.9f * sin(secondAngle)).toFloat()
//            ),
//            strokeWidth = 4f,
//            cap = StrokeCap.Round
//        )
//
//        // Center circle
//        drawCircle(Color.Black, radius = 10f, center = center)
//    }
//}
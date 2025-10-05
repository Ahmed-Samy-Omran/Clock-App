package com.example.clockapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockapp.utils.formatTimerDuration
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerClock(
    totalTimeMillis: Long,
    remainingTimeMillis: Long,
    modifier: Modifier = Modifier,
    isRunning: Boolean
) {
    val total = if (totalTimeMillis > 0L) totalTimeMillis else 1L
    val progress = (remainingTimeMillis.coerceAtLeast(0L).toFloat() / total.toFloat())
        .coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2.2f

        // ⏱ رسم العلامات (ticks)
        for (i in 0 until 60) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val start = Offset(
                (center.x + cos(angle) * radius).toFloat(),
                (center.y + sin(angle) * radius).toFloat()
            )
            val end = Offset(
                (center.x + cos(angle) * (radius - if (i % 5 == 0) 30 else 15)).toFloat(),
                (center.y + sin(angle) * (radius - if (i % 5 == 0) 30 else 15)).toFloat()
            )
            drawLine(color = Color.Gray, start = start, end = end, strokeWidth = 2f)
        }

        // ✅ ⏳ رسم الـ Arc الأزرق يظهر بس لو التايمر شغال
        if (isRunning) {
            drawArc(
                brush = Brush.horizontalGradient( listOf(Color(0xFF000000), Color(0xFF888888)) ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 20f, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        // 🔴 رسم العقرب الأحمر (يظهر دايمًا)
        val angleDeg = 360f * progress - 90f
        val handEnd = Offset(
            (center.x + cos(Math.toRadians(angleDeg.toDouble())) * (radius - 50)).toFloat(),
            (center.y + sin(Math.toRadians(angleDeg.toDouble())) * (radius - 50)).toFloat()
        )

        drawLine(
            color = Color.Red,
            start = center,
            end = handEnd,
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawCircle(Color.Red, radius = 12f, center = center)
    }

}

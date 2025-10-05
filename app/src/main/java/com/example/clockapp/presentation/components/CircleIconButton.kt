package com.example.clockapp.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CircleIconButton(
    icon: ImageVector? = null,
    painter: Painter? = null,
    backgroundColor: Color,
    iconColor: Color,
    borderColor: Color? = null,
    size: Dp = 64.dp,
    onClick: () -> Unit,
    animated: Boolean = false
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, CircleShape)
            .then(
                if (borderColor != null) Modifier.border(2.dp, borderColor, CircleShape)
                else Modifier
            )
            .clickable {
                if (animated) {
                    scope.launch {
                        scale.animateTo(1.15f, tween(140))
                        scale.animateTo(1f, tween(140))
                    }
                }
                onClick()
            }
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size((size.value * 0.45f).dp)
                )
            }
            painter != null -> {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size((size.value * 0.45f).dp)
                )
            }
        }
    }
}
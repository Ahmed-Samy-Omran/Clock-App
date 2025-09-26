import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClockTicker(
    value: String,                // hour, minute, or second
    fontSize: Int,             // control size depending on unit
    modifier: Modifier = Modifier,
    fontWeight: FontWeight,
    lineHeight: TextUnit? = null, // Add optional lineHeight
    color: Color? =null,
    fontFamily: FontFamily? = null
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            // Upward ticker animation
            (slideInVertically(animationSpec = tween(400)) { it } + fadeIn()) with
                    (slideOutVertically(animationSpec = tween(400)) { -it } + fadeOut())
        },
        label = "",
        modifier = modifier
    ) { newValue ->
        Text(
            text = newValue.toString().padStart(2, '0'),
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            color = Color.Black,
            lineHeight = lineHeight ?: androidx.compose.ui.unit.TextUnit.Unspecified // ← THIS WAS MISSING!
        )
    }
}

package com.example.clockapp.presentation.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest


@Composable
fun NumberColumn(
    range: IntRange,
    state: androidx.compose.foundation.lazy.LazyListState? = null,
    initialValue: Int = 0,
    onValueChange: (Int) -> Unit,
    itemHeight: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    // if parent provided a state, use it; otherwise create internal one
    val safeInitial = (initialValue - range.first).coerceIn(0, range.count() - 1)
    val internalState = state ?: rememberLazyListState(initialFirstVisibleItemIndex = safeInitial)

    LaunchedEffect(internalState) {
        snapshotFlow { internalState.firstVisibleItemIndex to internalState.firstVisibleItemScrollOffset }
            .collectLatest { (index, offset) ->
                val selectedIndex = if (offset > itemHeight.value / 2) index + 1 else index
                val selectedValue = range.first + selectedIndex
                if (selectedValue in range) onValueChange(selectedValue)
            }
    }

    Box(modifier = modifier.height(itemHeight * 3), contentAlignment = Alignment.Center) {
        LazyColumn(
            state = internalState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = internalState)
        ) {
            items(range.count()) { index ->
                val number = range.first + index
                val isSelected = internalState.firstVisibleItemIndex == index
                Text(
                    text = number.toString().padStart(2, '0'),
                    fontSize = if (isSelected) 28.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.3f
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(itemHeight)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .fillMaxWidth()
                )
            }
        }
    }
}

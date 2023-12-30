package com.example.livechatapp.ui.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.livechatapp.LCViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.livechatapp.CommonImage

enum class State {
    INITIAL, ACTIVE, COMPLETED
}

@Composable
fun SingleStatusScreen(
    navController: NavController,
    vm: LCViewModel,
    userId: String
) {
    val statuses = vm.status.value.filter {
        it.user.userId == userId
    }
    if (statuses.isNotEmpty()) {
        val currentStatus = remember {
            mutableStateOf(0)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            CommonImage(
                data = statuses[currentStatus.value].imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                statuses.forEachIndexed { index, status ->
                    CustomProgressIndication(
                        modifier = Modifier
                            .weight(1f)
                            .height(7.dp)
                            .padding(1.dp),
                        state = if (currentStatus.value < index) State.INITIAL else if (currentStatus.value == index) State.ACTIVE else State.COMPLETED
                    ) {
                        if(currentStatus.value < statuses.size-1) currentStatus.value++
                        else navController.popBackStack()
                    }
                }
            }
        }
    }

}

@Composable
fun CustomProgressIndication(
    modifier: Modifier,
    state: State,
    onComplete: () -> Unit
) {
    var progress = if (state == State.INITIAL) 0f else 1f
    if (state == State.ACTIVE) {
        val toggleState = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(toggleState) {
            toggleState.value = true
        }
        val p: Float by animateFloatAsState(
            targetValue = if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete.invoke() })
        progress = p
    }
    LinearProgressIndicator(
        modifier = modifier, color = androidx.compose.ui.graphics.Color.Red,
        progress = progress
    )
}
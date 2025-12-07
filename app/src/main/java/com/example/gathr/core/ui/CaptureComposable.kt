package com.example.gathr.core.ui

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val LocalCaptureTrigger = compositionLocalOf<(() -> Unit)?> { null }

@Composable
fun CaptureComposable(
    modifier: Modifier = Modifier,
    onTriggerProvided: (() -> Unit) -> Unit,
    onBitmapCaptured: (Bitmap) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val captureView = remember { mutableStateOf<ComposeView?>(null) }

    var triggerCapture by remember { mutableStateOf(false) }

    val performCapture: () -> Unit = {
        triggerCapture = true
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            ComposeView(context).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                captureView.value = this
                setContent {
                    content()
                }
            }
        }
    )

    DisposableEffect(performCapture) {
        onTriggerProvided(performCapture)
        onDispose {
            onTriggerProvided({})
        }
    }

    LaunchedEffect(triggerCapture) {
        if (triggerCapture) {
            captureView.value?.let { view ->
                withContext(Dispatchers.Main) {
                    try {
                        val bitmap = view.drawToBitmap()
                        onBitmapCaptured(bitmap)
                    } catch (e: Exception) {
                        Log.e("CAPTURE", "Error capturing isolated view: ${e.message}")
                    }
                    triggerCapture = false
                }
            }
        }
    }

    CompositionLocalProvider(LocalCaptureTrigger provides performCapture) {
    }
}

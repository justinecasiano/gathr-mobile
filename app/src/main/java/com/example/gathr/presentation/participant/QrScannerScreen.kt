@file:kotlin.OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.example.gathr.presentation.participant

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils
import com.example.gathr.utils.toTitleCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(viewModel: UserViewModel, onNavigateBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.userEffect.collect { effect ->
            when (effect) {
                UserEffect.NavigateNext -> {}
                UserEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        QrScannerContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )

        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun QrScannerContent(state: UserState, onIntent: (UserIntent) -> Unit) {
    val event: Event = state.currentEvent!!

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var camera: Camera? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(state.actionError) {
        if (state.actionError.isNotBlank()) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = state.actionError,
                duration = SnackbarDuration.Short
            )
            onIntent(UserIntent.ActionOnClear)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 20.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(UserIntent.BackClicked) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Close Scanner",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                actions = {
                    var isFlashOn by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                            isFlashOn = !isFlashOn
                            camera?.cameraControl?.enableTorch(isFlashOn)
                        },
                    ) {
                        Icon(
                            painter = if (isFlashOn) painterResource(R.drawable.flash_off)
                            else painterResource(R.drawable.flash_on),
                            contentDescription = "Toggle Flashlight",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(horizontal = 50.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(90.dp)
                        .clip(RoundedCornerShape(1.39.dp)),
                    thickness = 5.dp,
                    color = Color(0xFF1C1C1C).copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(25.dp))
                Text(
                    text = "Scan QR Code",
                    fontFamily = AppFonts.rethinkSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF5A3383)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = event.title.toTitleCase(),
                    fontFamily = AppFonts.rethinkSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Black.copy(0.8f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(50.dp))
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = Color.White,
                        contentColor = Color.Black.copy(0.8f),
                    )
                }
            )
        }
    ) { paddingValues ->
        if (cameraPermissionState.status.isGranted) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CameraPreviewComposable(
                    modifier = Modifier.fillMaxSize(),
                    onQrCodeScanned = { qrValue ->
                        scope.launch {
                            try {
                                val payload = Utils.verifySignedPayload(qrValue)

                                if (payload == null) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("❌ Invalid QR Code signature")
                                    return@launch
                                }

                                if (payload.eventId != event.id) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("❌ Ticket is for another event")
                                    return@launch
                                }

                                val existingAttendee =
                                    state.currentAttendees.find { it.userId.toString() == payload.userId }

                                if (existingAttendee?.participantStatus == ParticipantStatus.CHECKED_IN) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(
                                        message = "⚠️ ${existingAttendee.fullName} is already checked in",
                                        duration = SnackbarDuration.Short
                                    )
                                    return@launch
                                }

                                onIntent(UserIntent.ScanParticipantChanged(payload))
                                onIntent(UserIntent.MarkAttendance)

                            } catch (e: Exception) {
                                Log.e("Scanner", "Scanning error", e)
                                snackbarHostState.showSnackbar("An unexpected error occurred")
                            } finally {
                                onIntent(UserIntent.IsLoadingChanged(false))
                            }
                        }
                    },
                    onCameraReady = { cam -> camera = cam }
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f),
                        painter = painterResource(R.drawable.qr_scanner_box),
                        contentScale = ContentScale.Fit,
                        contentDescription = "Qr Scanner Box"
                    )
                }
            }
        } else if (!cameraPermissionState.status.shouldShowRationale) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission permanently denied.")
                Text("Please enable it in settings to use the scanner.")
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission is required to scan QR codes.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CameraPreviewComposable(
    modifier: Modifier = Modifier,
    onQrCodeScanned: (String) -> Unit,
    onCameraReady: (Camera) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = {
            val previewView = PreviewView(it).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            startCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                onQrCodeScanned = onQrCodeScanned,
                onCameraReady = onCameraReady
            )
            previewView
        },
        modifier = modifier
    )
}

@Serializable
data class ParticipantPayload(
    @SerialName("event_id")
    val eventId: Long,

    @SerialName("user_id")
    val userId: String
)

@OptIn(ExperimentalGetImage::class)
private class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private var lastScannedTime = 0L // To prevent rapid re-scanning

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        // Throttle scans to every 2 seconds to prevent spam
        if (mediaImage != null && System.currentTimeMillis() - lastScannedTime > 1000) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            onQrCodeScanned(rawValue)
                            lastScannedTime = System.currentTimeMillis() // Update last scan time
                            return@addOnSuccessListener // Stop processing more
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QrScanner", "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onQrCodeScanned: (String) -> Unit,
    onCameraReady: (Camera) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraExecutor = Executors.newSingleThreadExecutor()

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, QrCodeAnalyzer(onQrCodeScanned))
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            onCameraReady(camera)
        } catch (exc: Exception) {
            Log.e("QrScanner", "Use case binding failed", exc)
        }

    }, ContextCompat.getMainExecutor(context))
}

@file:kotlin.OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.example.gathr.presentation.auth.sign_up

import android.Manifest
import android.content.Context
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.ui.theme.AppFonts
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


enum class LivenessChallenge(val instruction: String) {
    LOOK_LEFT("Look Left"), LOOK_RIGHT("Look Right"), LOOK_UP("Look Up"), LOOK_DOWN("Look Down"), BLINK(
        "Blink Both Eyes"
    )
}

data class VerificationUiState(
    val instruction: String = "Please position your face in the oval",
    val progress: Float = 0f,
    val challenges: List<LivenessChallenge> = emptyList(),
    val isSuspicious: Boolean = false,
    val verificationComplete: Boolean = false,
    val isFaceDetected: Boolean = false
)

class HumanVerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var totalChallenges: Int = 0
    private var isTransitioning = false

    private val faceDetector: FaceDetector

    companion object {
        private const val EULER_Y_THRESHOLD = 20f  // Head turn left/right
        private const val EULER_X_THRESHOLD = 15f  // Head turn up/down
        private const val BLINK_THRESHOLD = 0.3f   // Eye open probability
    }

    private var hasSeenEyesOpen = true


    init {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()
        faceDetector = FaceDetection.getClient(options)
    }

    fun startVerification() {
        val isSuspicious = _uiState.value.isSuspicious
        val challenges = generateChallenges()
        totalChallenges = challenges.size

        _uiState.update {
            it.copy(
                instruction = "Starting Liveness Check...",
                progress = 0f,
                challenges = challenges,
                verificationComplete = false
            )
        }
    }

    private fun generateChallenges(): List<LivenessChallenge> {
        val allTurns = listOf(
            LivenessChallenge.LOOK_LEFT,
            LivenessChallenge.LOOK_RIGHT,
            LivenessChallenge.LOOK_UP,
            LivenessChallenge.LOOK_DOWN
        ).shuffled()

        return allTurns.take(2) + LivenessChallenge.BLINK
    }

    fun onFaceAnalyzed(face: Face) {
        if (_uiState.value.progress == 1f || isTransitioning) return

        Log.d("Liveness", "FACE DETECTED")
        _uiState.update { it.copy(isFaceDetected = true) }

        val currentChallenge = _uiState.value.challenges.firstOrNull()
        _uiState.update { it.copy(instruction = currentChallenge?.instruction ?: "") }
        Log.d("Liveness", currentChallenge.toString())

        if (currentChallenge == null || _uiState.value.verificationComplete) {
            return
        }

        var challengeMet = false
        when (currentChallenge) {
            LivenessChallenge.LOOK_LEFT -> {
                if (face.headEulerAngleY > EULER_Y_THRESHOLD) {
                    Log.d("Liveness", "Look Left: PASSED")
                    challengeMet = true
                }
            }

            LivenessChallenge.LOOK_RIGHT -> {
                if (face.headEulerAngleY < -EULER_Y_THRESHOLD) {
                    Log.d("Liveness", "Look Right: PASSED")
                    challengeMet = true
                }
            }

            LivenessChallenge.LOOK_UP -> {
                if (face.headEulerAngleX > EULER_X_THRESHOLD) {
                    Log.d("Liveness", "Look Up: PASSED")
                    challengeMet = true
                }
            }

            LivenessChallenge.LOOK_DOWN -> {
                if (face.headEulerAngleX < -EULER_X_THRESHOLD) {
                    Log.d("Liveness", "Look Down: PASSED")
                    challengeMet = true
                }
            }

            LivenessChallenge.BLINK -> {
                val leftEyeOpen = face.leftEyeOpenProbability
                val rightEyeOpen = face.rightEyeOpenProbability

                if (leftEyeOpen == null || rightEyeOpen == null) return

                if (hasSeenEyesOpen && leftEyeOpen < BLINK_THRESHOLD && rightEyeOpen < BLINK_THRESHOLD) {
                    Log.d("Liveness", "Blink: PASSED")
                    challengeMet = true
                    hasSeenEyesOpen = false
                } else if (leftEyeOpen > 0.8 && rightEyeOpen > 0.8) {
                    hasSeenEyesOpen = true
                }
            }
        }

        if (challengeMet && !isTransitioning) {
            isTransitioning = true
            _uiState.update {
                it.copy(
                    instruction = listOf(
                        "Great!", "Good!", "Perfect!", "Nice!", "Awesome!"
                    ).random()
                )
            }
            viewModelScope.launch {
                moveToNextChallenge()
                isTransitioning = false
            }
        }
    }

    fun onNoFaceDetected() {
        Log.d("Liveness", "FACE NOT DETECTED")
        _uiState.update { it.copy(isFaceDetected = false) }

        if (_uiState.value.challenges.isNotEmpty()) {
            _uiState.update { it.copy(instruction = "Please center your face in the oval") }
        }
    }

    private suspend fun moveToNextChallenge() {
        val remainingChallenges = _uiState.value.challenges.drop(1)

        if (remainingChallenges.isEmpty()) {
            _uiState.update {
                it.copy(
                    progress = 1f, instruction = "Verification Complete!", challenges = emptyList()
                )
            }
            delay(200)
            _uiState.update { it.copy(verificationComplete = true) }
            delay(2000)
            _uiState.update { it.copy(instruction = "Signing you up...") }
        } else {
            val progress = 1f - (remainingChallenges.size.toFloat() / totalChallenges)
            _uiState.update {
                it.copy(
                    progress = progress
                )
            }
            delay(2200)
            _uiState.update {
                it.copy(
                    instruction = remainingChallenges.first().instruction,
                    challenges = remainingChallenges
                )
            }
        }
    }

    override fun onCleared() {
        faceDetector.close()
        super.onCleared()
    }
}


@Composable
fun VerifyHumanScreen(
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpEffect.NavigateBack -> onNavigateBack()
                SignUpEffect.NavigateToLogin -> {}
                SignUpEffect.NavigateToNext -> onNavigateNext()
            }
        }
    }

    VerifyHumanContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerifyHumanContent(
    humanVerificationViewModel: HumanVerificationViewModel = viewModel(),
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val uiState by humanVerificationViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(uiState.instruction) {
        if (uiState.instruction == "Signing you up...") {
            onIntent(SignUpIntent.IsLoadingChanged(true))
            onIntent(SignUpIntent.SignUpClicked)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (cameraPermissionState.status.isGranted) {
                    LaunchedEffect(Unit) {
                        humanVerificationViewModel.startVerification()
                    }

                    CameraPreviewComposable(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        viewModel = humanVerificationViewModel
                    )
                    BoxWithConstraints(
                        Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    0.0f to Color(0xFF6E4C9C),
                                    0.7f to Color(0xFF261A36),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY)
                                )
                            )
                    ) {
                        FaceOvalOverlay(
                            modifier = Modifier.fillMaxSize(),
                            progress = uiState.progress,
                            isFaceDetected = uiState.isFaceDetected,
                        )
                        val yOffset = maxHeight * 0.05f
                        val newCenterY = (maxHeight / 2) - yOffset
                        val ovalHalfHeight = maxHeight * 0.25f

                        val ovalBottom = newCenterY + ovalHalfHeight

                        Text(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = ovalBottom + 32.dp)
                                .padding(horizontal = 70.dp),
                            text = uiState.instruction,
                            color = Color.White,
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (uiState.verificationComplete) {
                        val confetti = Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(300),
                            position = Position.Relative(0.5, 0.38)
                        )

                        KonfettiView(
                            modifier = Modifier.fillMaxSize(), parties = listOf(confetti)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (cameraPermissionState.status.shouldShowRationale) {
                                "Camera permission is needed to verify your identity."
                            } else {
                                "Please grant camera permission in settings."
                            }, textAlign = TextAlign.Center
                        )
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
        }
        if (state.isLoading) {
            LoadingOverlay()
        }
        if (state.signUpError.isNotBlank()) {
            Alert(
                onDismissRequest = { onIntent(SignUpIntent.SignUpErrorChanged("")) },
                title = "Error signing you up",
                message = state.signUpError,
                confirmButtonText = "Ok",
                onConfirmClicked = { onIntent(SignUpIntent.BackClicked) },
            )
        }
    }
}

@Composable
fun CameraPreviewComposable(
    modifier: Modifier = Modifier, viewModel: HumanVerificationViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = {
            val previewView = PreviewView(it).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            startFaceCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                onFaceAnalyzed = viewModel::onFaceAnalyzed,
                onNoFaceDetected = viewModel::onNoFaceDetected
            )
            previewView
        }, modifier = modifier
    )
}

private fun startFaceCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onFaceAnalyzed: (Face) -> Unit,
    onNoFaceDetected: () -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraExecutor = Executors.newSingleThreadExecutor()

    // ML Kit Face Detector
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()
    val faceDetector = FaceDetection.getClient(options)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalyzer =
            ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(
                        cameraExecutor, FaceAnalyzer(
                            faceDetector, onFaceAnalyzed, onNoFaceDetected
                        )
                    )
                }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e("Liveness", "Use case binding failed", exc)
        }

    }, ContextCompat.getMainExecutor(context))
}

@OptIn(ExperimentalGetImage::class)
private class FaceAnalyzer(
    private val detector: FaceDetector,
    private val onFaceAnalyzed: (Face) -> Unit,
    private val onNoFaceDetected: () -> Unit
) : ImageAnalysis.Analyzer {

    private val Y_OFFSET_PERCENT = 0.05f
    private val HALF_WIDTH_PERCENT = 0.45f
    private val HALF_HEIGHT_PERCENT = 0.25f

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val imageWidth = imageProxy.height
            val imageHeight = imageProxy.width

            val newCenterY = (imageHeight / 2f) - (imageHeight * Y_OFFSET_PERCENT)
            val halfWidth = imageWidth * HALF_WIDTH_PERCENT
            val halfHeight = imageHeight * HALF_HEIGHT_PERCENT

            val activeRect = RectF(
                (imageWidth / 2f) - halfWidth,
                newCenterY - halfHeight,
                (imageWidth / 2f) + halfWidth,
                newCenterY + halfHeight
            )

            detector.process(image).addOnSuccessListener { faces ->
                val centeredFace = faces.firstOrNull { face ->
                    // Check if the face's bounding box is inside our active rect
                    activeRect.contains(RectF(face.boundingBox))
                }

                if (centeredFace != null) {
                    onFaceAnalyzed(centeredFace) // Face is inside the oval
                } else {
                    onNoFaceDetected() // Face is outside the oval
                }
            }.addOnFailureListener { e ->
                Log.e("Liveness", "Face detection failed", e)
            }.addOnCompleteListener {
                imageProxy.close()
            }
        }
    }
}

@Composable
fun FaceOvalOverlay(
    modifier: Modifier = Modifier,
    progress: Float,
    isFaceDetected: Boolean,
) {
    val animatedBaseProgress by animateFloatAsState(
        targetValue = progress, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ), label = "BaseProgressAnim"
    )

    var fluctuationInitial by remember { mutableStateOf(0f) }
    var fluctuationTarget by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            val minMagnitude = 0.025f
            val maxMagnitude = 0.12f

            val magnitude = Random.nextFloat() * (maxMagnitude - minMagnitude) + minMagnitude

            fluctuationInitial = -magnitude
            fluctuationTarget = magnitude

            delay(Random.nextLong(1200, 1500))
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "fluctuation_transition")
    val fluctuation by infiniteTransition.animateFloat(
        initialValue = fluctuationInitial, // Fluctuates by +/- 1.5%
        targetValue = fluctuationTarget, animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "fluctuation_anim"
    )
    Canvas(modifier = modifier) {
        val yOffset = size.height * 0.05f
        val newCenterY = center.y - yOffset

        val halfWidth = size.width * 0.45f
        val halfHeight = size.height * 0.25f

        val ovalRect = Rect(
            left = center.x - halfWidth,
            top = newCenterY - halfHeight,
            right = center.x + halfWidth,
            bottom = newCenterY + halfHeight
        )

        drawOval(
            color = Color.Transparent,
            topLeft = ovalRect.topLeft,
            size = ovalRect.size,
            blendMode = BlendMode.Clear
        )

        val finalAnimatedProgress =
            if (progress == 1f) 1f else (animatedBaseProgress + fluctuation).coerceIn(0f, 1f)

        if (isFaceDetected || progress == 1f) {
            val progressAngle = finalAnimatedProgress * 360f
            drawArc(
                color = Color(0xFFD96944),
                startAngle = -90f,
                sweepAngle = progressAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx()),
                topLeft = ovalRect.topLeft,
                size = ovalRect.size
            )
        }
    }
}

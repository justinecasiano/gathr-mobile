package com.example.gathr.presentation.participant

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.drawToBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.CaptureComposable
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.LocalCaptureTrigger
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils
import com.example.gathr.utils.Utils.generateQrBitmap
import com.example.gathr.utils.Utils.saveBitmapToGallery
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
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
        EditProfileContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )
        if (state.isLoading) {
            LoadingOverlay()
        }
        when {
            state.actionError.isNotBlank() -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(state: UserState, onIntent: (UserIntent) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    Box(Modifier.padding(top = 10.dp, start = 10.dp)) {
                        IconButton(onClick = { }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxHeight()) {

        }
    }
}


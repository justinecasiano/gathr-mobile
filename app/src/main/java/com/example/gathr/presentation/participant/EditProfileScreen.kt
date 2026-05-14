package com.example.gathr.presentation.participant

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
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
import coil3.compose.AsyncImage
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.CaptureComposable
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.ImageViewer
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
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import com.example.gathr.utils.Utils
import com.example.gathr.utils.Utils.generateQrBitmap
import com.example.gathr.utils.Utils.saveBitmapToGallery
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.UUID

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel, onNavigateBack: () -> Unit
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
            state.actionTitle.contains("Success") -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = {
                        onNavigateBack()
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                    },
                )
            }

            state.actionError.contains("Are you sure") -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

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
    val user = state.currentUser ?: return

    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var displayName by remember { mutableStateOf(user.displayName ?: "") }
    var selectedImageUriString by remember { mutableStateOf<String?>(null) }

    val hasChanges = firstName != user.firstName ||
            lastName != user.lastName ||
            displayName != (user.displayName ?: "") ||
            selectedImageUriString != null

    var submittedOnce by remember { mutableStateOf(false) }
    val errors = Utils.validateProfile(firstName, lastName, displayName)

    var viewerImage by remember { mutableStateOf("") }

    val context = LocalContext.current

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = result.data?.let { UCrop.getOutput(it) }
            selectedImageUriString = resultUri?.toString()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val destinationUri = Uri.fromFile(File(context.cacheDir, "${UUID.randomUUID()}.jpg"))

            val options = UCrop.Options().apply {
                setCompressionFormat(Bitmap.CompressFormat.WEBP)
                setCompressionQuality(80)
                setHideBottomControls(false)
                setFreeStyleCropEnabled(false)
            }

            val uCrop = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f)
                .withMaxResultSize(400, 400).withOptions(options).getIntent(context)

            uCropLauncher.launch(uCrop)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    title = {
                        Text(
                            text = "Edit Profile",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = rethinkSans,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(UserIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(Modifier.fillMaxHeight()) {
                Column(
                    Modifier
                        .fillMaxHeight(0.84f)
                        .background(Color.White)
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            ),
                            end = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            ),
                        )
                        .padding(horizontal = 30.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(20.dp))

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = Color(0xFF473163),
                            onClick = {
                                if (selectedImageUriString != null) viewerImage =
                                    selectedImageUriString!!
                                else if (!user.avatarUrl.isNullOrBlank()) viewerImage =
                                    user.avatarUrl
                            }) {
                            if (selectedImageUriString != null || !user.avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = selectedImageUriString ?: user.avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        user.firstName.take(1).uppercase(), style = TextStyle(
                                            color = Color.White,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        SmallFloatingActionButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = CircleShape,
                            containerColor = Color(0xFF7B55A3),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(36.dp)
                                .offset(x = (-4).dp, y = (-4).dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.edit),
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    CounterTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name",
                        maxChar=50,
                        isError = submittedOnce && errors.firstNameError.isNotBlank(),
                        supportingText = errors.firstNameError
                    )
                    Spacer(Modifier.height(10.dp))
                    CounterTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last Name",
                        maxChar=50,
                        isError = submittedOnce && errors.lastNameError.isNotBlank(),
                        supportingText = errors.lastNameError
                    )
                    Spacer(Modifier.height(10.dp))
                    CounterTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = "Username",
                        maxChar = 20,
                        prefix = "@",
                        isError = submittedOnce && errors.displayNameError.isNotBlank(),
                        supportingText = errors.displayNameError
                    )
                }

                Column(
                    Modifier
                        .background(Color.White)
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .border(2.dp, color = Color(0xFFD7D7D7))
                            .padding(horizontal = 55.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        contentAlignment = Alignment.Center
                    ) {
                        ElevatedButton(
                            text = "SAVE CHANGES",
                            onClick = {
                                submittedOnce = true
                                when {
                                    errors.hasErrors -> {}

                                    !hasChanges -> {
                                        onIntent(UserIntent.ActionTitleChanged("No Changes"))
                                        onIntent(UserIntent.ActionErrorChanged("You haven't made any changes to your profile yet."))
                                    }

                                    else -> {
                                        onIntent(UserIntent.ActionTitleChanged("Update Profile"))
                                        onIntent(UserIntent.ActionErrorChanged("Are you sure you want to update your profile?"))
                                        onIntent(UserIntent.ActionOnConfirmClicked {
                                            onIntent(
                                                UserIntent.UpdateUserProfile(
                                                    firstName = firstName,
                                                    lastName = lastName,
                                                    displayName = displayName,
                                                    newAvatarUri = selectedImageUriString
                                                )
                                            )
                                        })
                                    }
                                }
                            },
                            isEnabled = firstName.isNotBlank() && lastName.isNotBlank() && displayName.isNotBlank(),
                            buttonColor = Color(0xFF7B55A3),
                            outlineColor = Color(0xFF4C2576),
                            buttonShape = RoundedCornerShape(20.dp),
                            textStyle = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                        )
                    }
                }
            }
        }
        if (viewerImage.isNotBlank()) {
            ImageViewer(viewerImage, onDismiss = { viewerImage = "" })
        }
    }
}

@Composable
fun CounterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxChar: Int? = null,
    prefix: String? = null,
    isError: Boolean = false,
    supportingText: String = ""
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            label, style = TextStyle(
                fontFamily = AppFonts.rethinkSans, fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            prefix = prefix?.let {
                {
                    Text(
                        it, style = TextStyle(fontFamily = AppFonts.rethinkSans)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B55A3),
                unfocusedBorderColor = Color(0xFFD7D7D7),
                errorBorderColor = Color.Red
            ),
            supportingText = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (isError) supportingText else "",
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 11.sp,
                            color = Color.Red
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (maxChar != null) {
                        Text(
                            text = "${value.length} / $maxChar",
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 11.sp
                            ),
                            color = if (value.length > maxChar) Color.Red else Color.Unspecified
                        )
                    }
                }
            }
        )
    }
}

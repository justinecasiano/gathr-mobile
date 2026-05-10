package com.example.gathr.presentation.auth.sign_up

import android.R.attr.layoutDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gathr.R
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppMisc
import com.example.gathr.ui.theme.fadeIn
import com.example.gathr.ui.theme.fadeOut
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PrivacyPolicyScreen(
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpEffect.NavigateBack -> onNavigateBack()
                SignUpEffect.NavigateToLogin -> onNavigateLogin()
                SignUpEffect.NavigateToNext -> onNavigateNext()
            }
        }
    }
    PrivacyPolicyContent(state = state, onIntent = viewModel::handleIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyContent(state: SignUpState, onIntent: (SignUpIntent) -> Unit) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val hasScrolledDown by remember {
        derivedStateOf { scrollState.value == scrollState.maxValue }
    }
    val isFabVisible by remember {
        derivedStateOf { scrollState.value > 0 }
    }

    val boldTextStyle = TextStyle(
        fontFamily = AppFonts.instrumentSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    val regularTextStyle = TextStyle(
        fontFamily = AppFonts.instrumentSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color.White
    )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp),
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
            navigationIcon = {
                IconButton(onClick = { onIntent(SignUpIntent.BackClicked) }) {
                    Icon(
                        modifier = Modifier.size(37.dp),
                        tint = Color(0xFFBFB6CA),
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back"
                    )
                }
            },
        )
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = isFabVisible && !state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                shape = CircleShape,
                modifier = Modifier.offset(y = -135.dp),
                containerColor = Color(0xFFF7906E),
                onClick = {
                    scope.launch {
                        scrollState.animateScrollTo(0)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_up),
                    modifier = Modifier.size(35.dp),
                    tint = Color.White,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color(0xFF261A36))
        ) {
            Column(
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
                        .padding(horizontal = 20.dp),
                ) {
                    Text(
                        "Privacy Policy",
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 12.dp, start = 8.dp, end = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color(0xFF574272).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(
                                    top = 18.dp,
                                    bottom = 18.dp,
                                    start = 15.dp,
                                    end = 15.dp
                                )
                        ) {
                            Text("1. Information We Collect", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "We may collect the following information:",
                                    style = regularTextStyle
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Personal Information",
                                    style = regularTextStyle
                                )
                                BulletItem("Full name")
                                BulletItem("Email address / school email")
                                BulletItem("Organization name (for organizers)")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Activity and App Usage Data",
                                    style = regularTextStyle
                                )
                                BulletItem("Events viewed")
                                BulletItem("Events registered for")
                                BulletItem("Attendance records")
                                BulletItem("Scanned QR codes")
                                BulletItem("Device information (model, OS version)")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("2. How Your Data Is Used", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "We use your data to:",
                                    style = regularTextStyle
                                )
                                BulletItem("Manage your account")
                                BulletItem("Display your profile to event organizers")
                                BulletItem("Register you for events")
                                BulletItem("Generate attendance records")
                                BulletItem("Send event reminders")
                                BulletItem("Improve the app")
                                BulletItem("Prevent fraud and misuse")
                                Text(
                                    "We do not sell or share your personal data with third parties for advertising.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("3. Sharing of Information", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Your information may be shared with:",
                                    style = regularTextStyle
                                )
                                BulletItem("School organizations managing events you register for")
                                BulletItem("IT personnel or developers maintaining the app (only when necessary)")
                                Text(
                                    "Your data is never sold, traded, or given to advertisers.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("4. Data Storage and Security", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Gathr uses secure servers and encryption methods to protect your data.",
                                    style = regularTextStyle
                                )
                                Text(
                                    "However, no system is 100% secure. Users are responsible for:",
                                    style = regularTextStyle
                                )
                                BulletItem("Protecting their password")
                                BulletItem("Avoiding account sharing")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("5. User Rights (RA 10173)", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "You have the right to:",
                                    style = regularTextStyle
                                )
                                BulletItem("Access your personal data")
                                BulletItem("Correct inaccurate data")
                                BulletItem("Request deletion of your account")
                                BulletItem("Withdraw consent")
                                BulletItem("File complaints with the National Privacy Commission (NPC)")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("6. Retention of Data", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                BulletItem("Attendance and event logs are kept as long as required by the school.")
                                BulletItem("Account information is retained until you request deletion.")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("7. Changes to This Privacy Policy", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                BulletItem("We may update this policy from time to time. Notification will be sent through the app or email.")
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
            BottomButton(
                bottomPadding = paddingValues.calculateBottomPadding(),
                textButtonText = "I ALREADY HAVE AN ACCOUNT",
                onTextButtonClick = { onIntent(SignUpIntent.LoginClicked) },
                buttonText = "NEXT",
                isButtonEnabled = hasScrolledDown,
                onButtonClick = {
                    onIntent(SignUpIntent.NextClicked)
                },
            )
        }
    }
}

@Preview
@Composable
private fun TermsOfServiceScreenPreview() {
    TermsOfServiceContent(
        state = SignUpState(), onIntent = {})
}
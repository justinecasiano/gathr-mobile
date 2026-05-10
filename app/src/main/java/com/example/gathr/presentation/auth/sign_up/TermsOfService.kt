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
fun TermsOfServiceScreen(
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
    TermsOfServiceContent(state = state, onIntent = viewModel::handleIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceContent(state: SignUpState, onIntent: (SignUpIntent) -> Unit) {
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
                        "Terms of Service",
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
                            Text("1. Introduction", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Gathr is an event ticketing and attendance application created for schools, organizations, and students. School organizations can create events, and students can register and attend events through the app.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("2. Eligibility", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "By using Gathr, you confirm that:",
                                    style = regularTextStyle
                                )
                                BulletItem("You are a student, faculty member, or school organization representative authorized to use the platform.")
                                BulletItem("You are providing accurate information.")
                                BulletItem("You agree to follow all school policies and Philippine laws while using the app.")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("3. Account Creation and Responsibilities", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "To use Gathr, users must create an account with their:",
                                    style = regularTextStyle
                                )
                                BulletItem("Full name")
                                BulletItem("Email address or school email")
                                BulletItem("Student ID number (if applicable)")
                                Spacer(modifier = Modifier.height(9.dp))
                                Text(
                                    "Users must:",
                                    style = regularTextStyle
                                )
                                BulletItem("Keep login details private.")
                                BulletItem("Immediately report unauthorized account access.")
                                BulletItem("Be responsible for all activity done using their account.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Gathr is not responsible for any damage or unauthorized use caused by weak passwords or account sharing.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("4. Use of the App", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Users agree NOT to:",
                                    style = regularTextStyle
                                )
                                BulletItem("Misuse the app or create fake events.")
                                BulletItem("Impersonate another person or organization.")
                                BulletItem("Post illegal, abusive, or fraudulent content.")
                                BulletItem("Interfere with the platform’s security.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Gathr may suspend or remove any user who violates these rules.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("5. Event Registration and Attendance", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                BulletItem("School organizations may create, edit, or cancel events.")
                                BulletItem("Students may register for events through the app.")
                                BulletItem("Gathr does not control event schedules, locations, or policies.")
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Event organizers are fully responsible for:",
                                    style = regularTextStyle
                                )
                                BulletItem("Accuracy of event details")
                                BulletItem("Safety protocols")
                                BulletItem("Attendance records")
                                BulletItem("Capacity and slot management")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Gathr only provides the platform.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("6. Intellectual Property", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                BulletItem("All logos, designs, graphics, features, and code are owned by Gathr or its developers.")
                                Text("Users may not:", style = regularTextStyle)
                                BulletItem("Copy, modify, or reproduce app content")
                                BulletItem("Reverse-engineer the app")
                                BulletItem("Use Gathr for commercial purposes without permission")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("7. User Content", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "If a user creates content (e.g., profile image or event banner), they:",
                                    style = regularTextStyle
                                )
                                BulletItem("Confirm they own the content")
                                BulletItem("Allow Gathr to display it inside the app")
                                BulletItem("Accept responsibility for any legal issues arising from it")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("8. Service Availability", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Gathr is provided “as is”, without guarantees that:",
                                    style = regularTextStyle
                                )
                                BulletItem("It will always be error-free")
                                BulletItem("It will never experience downtime")
                                BulletItem("All event information is accurate (event organizers are responsible for this)")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("9. Limitation of Liability", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Gathr is NOT liable for:",
                                    style = regularTextStyle
                                )
                                BulletItem("Event cancellations or changes by organizers")
                                BulletItem("Incorrect event information posted by organizations")
                                BulletItem("Lost attendance records caused by organizer errors")
                                BulletItem("User mistakes (wrong registration, incorrect details)")
                                BulletItem("Damages resulting from hacking or misuse beyond our control")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Under Philippine law, liability for fraud or gross negligence cannot be waived, and Gathr will comply with this.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("10. Termination of Account", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Gathr may suspend or delete accounts that:",
                                    style = regularTextStyle
                                )
                                BulletItem("Violate school policies")
                                BulletItem("Break Philippine laws")
                                BulletItem("Post harmful or fraudulent content")
                                BulletItem("Misuse the platform")
                                Text(
                                    "Users may request account deletion anytime.",
                                    style = regularTextStyle
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("11. Updates to the Terms", style = boldTextStyle)
                            Column(Modifier.padding(start = 8.dp)) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "We may update these Terms at any time. Continued use of the app means you accept the updated terms.",
                                    style = regularTextStyle
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            buildAnnotatedString {
                                append("By creating an account or using Gathr, you agree to be bound by these Terms and Conditions and our Privacy Policy.")
                            },
                            style = regularTextStyle,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
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

@Composable
fun BulletItem(
    text: String = "",
    varyingText: @Composable (() -> Unit)? = null,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "\t•  ", style = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                ), modifier = Modifier.padding(top = 2.dp)
            )
            if (varyingText == null) {
                Text(
                    text = text, lineHeight = 16.sp, style = TextStyle(
                        fontFamily = AppFonts.instrumentSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    ), modifier = Modifier.weight(1f)
                )
            } else {
                varyingText()
            }
        }
        Spacer(modifier = Modifier.height(0.5.dp))
    }
}

@Preview
@Composable
private fun TermsOfServiceScreenPreview() {
    TermsOfServiceContent(
        state = SignUpState(), onIntent = {})
}
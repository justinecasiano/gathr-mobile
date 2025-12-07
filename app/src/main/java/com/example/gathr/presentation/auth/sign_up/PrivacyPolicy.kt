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
                            Spacer(modifier = Modifier.height(3.dp))
                            BulletItem("We may collect the following information:")
                            BulletItem("You must provide accurate registration information and keep it updated.")
                            BulletItem("We may suspend or terminate accounts that violate school policies or these Terms.")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("2. Events Creation & Participation", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            BulletItem("Organizers may create and manage school-related events.")
                            BulletItem("Students may browse and register for available events.")
                            BulletItem {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontFamily = AppFonts.instrumentSans,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("Gathr ")
                                        }
                                        append("does not host or manage events. Schools and organizations are solely responsible for event safety, rules, and admission decisions.")
                                    },
                                    style = regularTextStyle,
                                    lineHeight = 16.sp,
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("3. User Conduct", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "You agree to use the Service in a lawful and respectful manner. Prohibited behavior includes, but is not limited to:",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            BulletItem("Harassment, bullying, or threatening content")
                            BulletItem("Events or actions that violate school rules or laws")
                            BulletItem("Unauthorized access to other users’ information")
                            BulletItem("Attempts to disrupt or exploit the Service")
                            Spacer(modifier = Modifier.height(9.dp))
                            Text(
                                "Violations may lead to account restrictions or removal.",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("4. User Content", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "You may submit content such as event details, comments, and media (\"User Content\"). You retain ownership but grant Gather a non-exclusive, worldwide license to use that content solely for operating the Service. We may remove content that is inappropriate or poses safety, privacy, or legal concerns.",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("5. Privacy", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "Your use of Gather is also governed by our Privacy Policy, which explains how student information is collected and protected. Certain school staff and organizers may access student attendance information for school-related purposes.",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("6. Moderator Rights", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Gathr moderator may:", style = regularTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            BulletItem("Remove events violating policies or safety standards")
                            BulletItem("Restrict user access for misconduct or rule violations")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("7. Intellectual Property", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "All intellectual property belonging to Gather, including branding, code, and design (excluding User Content), is protected by copyright and trademark law.",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("8. Disclaimers", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "Gathr is provided “as is” without warranties or guarantees of any kind. We do not promise:",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            BulletItem("The accuracy or availability of event listings")
                            BulletItem("Error-free or uninterrupted operation")
                            BulletItem("That any event will meet expectations")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("9. Limitation of Liability", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "To the fullest extent permitted by law, Gather is not liable for:",
                                style = regularTextStyle
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            BulletItem("Injuries or damages resulting from participation in events")
                            BulletItem("Loss of data or access disruptions")
                            BulletItem("User or organizer actions or misconduct")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("10. Changes", style = boldTextStyle)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "We may modify these Terms or the Service at any time. Continued use after changes means you accept the updated Terms.",
                                style = regularTextStyle
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            buildAnnotatedString {
                                append("By tapping \"")
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Accept")
                                }
                                append("\", you confirm that you have read, understood, and agree to be bound by these Terms of Service.")
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
                buttonText = "ACCEPT",
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
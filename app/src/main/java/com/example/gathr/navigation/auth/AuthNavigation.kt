package com.example.gathr.navigation.auth

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.core.ui.StatusScreen
import com.example.gathr.presentation.auth.LandingScreen
import com.example.gathr.presentation.auth.sign_up.SchoolVerificationContent
import com.example.gathr.presentation.auth.sign_up.SchoolVerificationScreen
import com.example.gathr.presentation.auth.sign_up.SignUpScreen
import com.example.gathr.presentation.auth.sign_up.SignUpViewModel
import com.example.gathr.presentation.auth.sign_up.TermsOfServiceScreen
import com.example.gathr.presentation.auth.sign_up.VerifyEmailScreen
import com.example.gathr.ui.theme.AppFonts
import org.koin.androidx.compose.koinViewModel
import kotlin.math.sign

@Composable
fun AuthNavigation() {
    val backStack = rememberNavBackStack(AuthScreen.TermsOfService)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<AuthScreen.Landing> {
                LandingScreen(
                    onNavigateSignUp = {
                        backStack.add(AuthScreen.SignUp)
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                )
            }
            entry<AuthScreen.VerifyHuman> {
            }
            entry<AuthScreen.SignUp> {
                val signUpViewModel: SignUpViewModel = koinViewModel(
                    key = remember { "sign-up" }
                )

                SignUpScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.SchoolVerification)
                    },
                )
            }
            entry<AuthScreen.SchoolVerification> {
                val signUpViewModel: SignUpViewModel = koinViewModel(
                    key = remember { "sign-up" }
                )

                SchoolVerificationScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.TermsOfService)
                    },
                )
            }
            entry<AuthScreen.TermsOfService> {
                val signUpViewModel: SignUpViewModel = koinViewModel(
                    key = remember { "sign-up" }
                )

                TermsOfServiceScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.VerificationCode)
                    },
                )
            }
            entry<AuthScreen.VerificationCode> {
                val signUpViewModel: SignUpViewModel = koinViewModel(
                    key = remember { "sign-up" }
                )
                val currentState by signUpViewModel.state.collectAsStateWithLifecycle()

                StatusScreen(
                    title = "Verification Code Sent",
                    buttonText = "NEXT",
                    message = {
                        Text(
                            buildAnnotatedString {
                                append("We have sent a verification code to your email ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(currentState.email)
                                }
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    },
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.VerifyEmail)
                    }
                )
            }
            entry<AuthScreen.VerifyEmail> {
                val signUpViewModel: SignUpViewModel = koinViewModel(
                    key = remember { "sign-up" }
                )

                VerifyEmailScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.AccountCreated)
                    },
                )
            }
            entry<AuthScreen.AccountCreated> {
            }
            entry<AuthScreen.Login> {
            }
            entry<AuthScreen.ForgotPassword> {
            }
            entry<AuthScreen.ResetConfirmation> {
            }
            entry<AuthScreen.ResetPassword> {
            }
            entry<AuthScreen.PasswordReset> {
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
    )
}

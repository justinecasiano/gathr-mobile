package com.example.gathr.navigation.auth

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.core.ui.StatusScreen
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.presentation.auth.LandingScreen
import com.example.gathr.presentation.auth.NoInternetScreen
import com.example.gathr.presentation.auth.forgot_password.ForgotPasswordScreen
import com.example.gathr.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.example.gathr.presentation.auth.forgot_password.ResetPasswordScreen
import com.example.gathr.presentation.auth.forgot_password.VerifyResetPasswordScreen
import com.example.gathr.presentation.auth.login.LoginScreen
import com.example.gathr.presentation.auth.login.LoginViewModel
import com.example.gathr.presentation.auth.sign_up.PrivacyPolicyScreen
import com.example.gathr.presentation.auth.sign_up.VerifyHumanScreen
import com.example.gathr.presentation.auth.sign_up.SchoolVerificationScreen
import com.example.gathr.presentation.auth.sign_up.SignUpScreen
import com.example.gathr.presentation.auth.sign_up.SignUpViewModel
import com.example.gathr.presentation.auth.sign_up.TermsOfServiceScreen
import com.example.gathr.presentation.auth.sign_up.VerifyEmailScreen
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.NetworkConnectivityService
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AuthNavigation(onLogin: () -> Unit) {
    val owner = LocalViewModelStoreOwner.current!!

    val signUpViewModel: SignUpViewModel = koinViewModel()
    val forgotPasswordViewModel: ForgotPasswordViewModel = koinViewModel()

    val auth: Auth = koinInject()
    val authRepository: AuthRepository = koinInject()
    val isLoggedIn = authRepository.isLoggedIn()

    val startingDestination = AuthScreen.Landing
    val backStack = rememberNavBackStack(startingDestination)

    val networkService: NetworkConnectivityService = koinInject()
    val isOnline by networkService.observeNetworkStatus()
        .collectAsStateWithLifecycle(initialValue = true)

    LaunchedEffect(isOnline) {
        delay(500L)
        if (!isOnline) {
            backStack.add(AuthScreen.NoInternet)
        } else {
            if (backStack.lastOrNull() == AuthScreen.NoInternet) {
                backStack.removeLastOrNull()
            }
        }

        if (isOnline && isLoggedIn) {
            Log.d("SUPABASE", "User is authenticated")
            val lastScreen = backStack.lastOrNull()
            if (lastScreen == AuthScreen.Landing) {
                Log.d("SUPABASE", "User logged in, redirecting")
                onLogin()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            val lastScreen = backStack.lastOrNull()
            if (lastScreen != null && listOf(
                    AuthScreen.NoInternet,
                    AuthScreen.VerifyHuman,
                    AuthScreen.VerificationCode,
                    AuthScreen.AccountCreated,
                    AuthScreen.ResetLink,
                    AuthScreen.PasswordChanged,
                ).contains(lastScreen)
            ) null
            else backStack.removeLastOrNull()
        },
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
            entry<AuthScreen.NoInternet> {
                NoInternetScreen()
            }
            entry<AuthScreen.SignUp> {
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
                TermsOfServiceScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.PrivacyPolicy)
                    },
                )
            }
            entry<AuthScreen.PrivacyPolicy> {
                PrivacyPolicyScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateLogin = {
                        backStack.add(AuthScreen.Login)
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.VerifyHuman)
                    },
                )
            }
            entry<AuthScreen.VerifyHuman> {
                VerifyHumanScreen(
                    viewModel = signUpViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.VerificationCode)
                    },
                )
            }
            entry<AuthScreen.VerificationCode> {
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
                            }, style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    }, onNavigateBack = {
                        backStack.removeLastOrNull()
                    }, onNavigateNext = {
                        backStack.add(AuthScreen.VerifyEmail)
                    })
            }
            entry<AuthScreen.VerifyEmail> {
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
                owner.viewModelStore.clear()
                StatusScreen(
                    title = "Account created",
                    buttonText = "SIGN IN",
                    message = {
                        Text(
                            "You may now sign in to your account",
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    }, onNavigateBack = {
                        backStack.removeLastOrNull()
                    }, onNavigateNext = {
                        backStack.clear()
                        backStack.add(AuthScreen.Login)
                    })
            }
            entry<AuthScreen.Login> {
                val loginViewModel: LoginViewModel = koinViewModel()

                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateForgotPassword = {
                        backStack.add(AuthScreen.ForgotPassword)
                    },
                    onNavigateNext = {
                        onLogin()
                    },
                )
            }
            entry<AuthScreen.ForgotPassword> {
                ForgotPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.ResetLink)
                    },
                )
            }
            entry<AuthScreen.ResetLink> {
                val currentState by forgotPasswordViewModel.state.collectAsStateWithLifecycle()
                StatusScreen(
                    title = "Reset Password",
                    buttonText = "NEXT",
                    message = {
                        Text(
                            buildAnnotatedString {
                                append("We have sent a 6-digit code to your email ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(currentState.email)
                                }
                            }, style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    }, onNavigateBack = {
                        backStack.removeLastOrNull()
                    }, onNavigateNext = {
                        backStack.add(AuthScreen.VerifyResetPassword)
                    })
            }
            entry<AuthScreen.VerifyResetPassword> {
                VerifyResetPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.ResetPassword)
                    },
                )
            }
            entry<AuthScreen.ResetPassword> {
                ResetPasswordScreen(
                    viewModel = forgotPasswordViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(AuthScreen.PasswordChanged)
                    },
                )
            }
            entry<AuthScreen.PasswordChanged> {
                owner.viewModelStore.clear()
                LaunchedEffect(Unit) {
                    auth.clearSession()
                }
                StatusScreen(
                    title = "Password changed",
                    buttonText = "SIGN IN",
                    message = {
                        Text(
                            "You may now sign in to your account",
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    }, onNavigateBack = {
                        backStack.removeLastOrNull()
                    }, onNavigateNext = {
                        backStack.clear()
                        backStack.add(AuthScreen.Login)
                    })
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
    )
}

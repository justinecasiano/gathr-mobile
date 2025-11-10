package com.example.gathr.navigation.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class AuthScreen : NavKey {
    @Serializable
    data object Landing : AuthScreen()

    @Serializable
    data object VerifyHuman : AuthScreen()

    @Serializable
    data object SignUp : AuthScreen()

    @Serializable
    data object SchoolVerification : AuthScreen()

    @Serializable
    data object TermsOfService : AuthScreen()

    @Serializable
    data object VerificationCode : AuthScreen()

    @Serializable
    data object VerifyEmail : AuthScreen()

    @Serializable
    data object AccountCreated : AuthScreen()

    @Serializable
    data object Login : AuthScreen()

    @Serializable
    data object ForgotPassword : AuthScreen()

    @Serializable
    data object ResetConfirmation : AuthScreen()

    @Serializable
    data object ResetPassword : AuthScreen()

    @Serializable
    data object PasswordReset : AuthScreen()
}

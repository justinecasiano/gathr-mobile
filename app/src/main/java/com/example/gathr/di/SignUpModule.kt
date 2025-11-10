package com.example.gathr.di

import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.presentation.auth.sign_up.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val signUpModule = module {
    single { AuthRepository() }
    viewModel { SignUpViewModel(get()) }
}

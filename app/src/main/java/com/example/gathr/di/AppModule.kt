package com.example.gathr.di

import LocalCacheManager
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.AuthRepositoryImpl
import com.example.gathr.data.repository.EventParticipantRepository
import com.example.gathr.data.repository.EventParticipantRepositoryImpl
import com.example.gathr.data.repository.NotificationRepository
import com.example.gathr.data.repository.NotificationRepositoryImpl
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.data.repository.UserRepositoryImpl
import com.example.gathr.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.example.gathr.presentation.auth.login.LoginViewModel
import com.example.gathr.presentation.auth.sign_up.SignUpViewModel
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.utils.NetworkConnectivityService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.WebSockets
import okhttp3.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(SupabaseInternal::class)
val appModule = module {
    single { NetworkConnectivityService(androidContext()) }
    single { LocalCacheManager(androidContext()) }


    single {
        createSupabaseClient(
            supabaseUrl = "https://gjhijvulvzqbmqafragh.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdqaGlqdnVsdnpxYm1xYWZyYWdoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI5MzcxODEsImV4cCI6MjA3ODUxMzE4MX0.mhdtm8zL8Tr8w_X0YB2QhjnRTvyicOkXd4KQvrtzAzU"
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)

            httpConfig {
                install(WebSockets)

                install(HttpTimeout) {
                    connectTimeoutMillis = 30_000L

//                    requestTimeoutMillis = 1L
                    requestTimeoutMillis = 30_000L

                    socketTimeoutMillis = 30_000L
                }
            }
        }
    }

    single<Postgrest> {
        get<SupabaseClient>().postgrest
    }

    single<Auth> {
        get<SupabaseClient>().auth
    }

    single<Storage> {
        get<SupabaseClient>().storage
    }

    single<Realtime> {
        get<SupabaseClient>().realtime
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }

    single<EventParticipantRepository> {
        EventParticipantRepositoryImpl(androidContext(), get(), get(), get(), get())
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(get(), get())
    }

    viewModel { SignUpViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get(), get(), get(), get()) }
}

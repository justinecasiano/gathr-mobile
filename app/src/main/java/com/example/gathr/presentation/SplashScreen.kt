package com.example.gathr.presentation

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppMisc
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.data.model.UserRole
import com.example.gathr.presentation.main.FetchStatus
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.utils.NetworkConnectivityService

@Composable
fun SplashScreen(
    userViewModel: UserViewModel,
    onLoaded: (Boolean) -> Unit
) {
    val auth: Auth = koinInject()
    val authRepository: AuthRepository = koinInject()

    val networkService: NetworkConnectivityService = koinInject()
    val isOnline by networkService.observeNetworkStatus().collectAsState(initial = true)

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            val hasCachedData = userViewModel.loadCacheImmediately()
            if (hasCachedData) {
                onLoaded(true)
            } else {
                onLoaded(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        auth.awaitInitialization()
        val user = userViewModel.getCurrentUser()

        if (user !== null) {
            userViewModel.handleIntent(UserIntent.FetchNotifications)

            if (user.role === UserRole.PARTICIPANT) {
                userViewModel.handleIntent(UserIntent.FetchManagedEvents)
                userViewModel.handleIntent(UserIntent.FetchJoinableEvents)
                userViewModel.handleIntent(UserIntent.FetchJoinedEvents)
            } else if (user.role === UserRole.MODERATOR) {
                userViewModel.handleIntent(UserIntent.FetchModeratorEvents)
            }
        } else {
            delay(2000)
            onLoaded(false)
        }
    }

    val state by userViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.dataFetchStatus) {
        val user = userViewModel.getCurrentUser()
        val status = state.dataFetchStatus
        val hasFetchedData =
            when {
                user?.role == UserRole.PARTICIPANT ->
                    status.currentUser == FetchStatus.DONE && status.managedEvents == FetchStatus.DONE &&
                            status.joinableEvents == FetchStatus.DONE && status.joinedEvents == FetchStatus.DONE &&
                            status.notifications == FetchStatus.DONE

                else -> status.currentUser == FetchStatus.DONE && status.moderatorEvents == FetchStatus.DONE && status.notifications == FetchStatus.DONE
            }

        Log.d("FETCH_STATUS", status.toString())
        if (user !== null && hasFetchedData) {
            onLoaded(authRepository.isLoggedIn())
        }
    }

    val role = state.currentUser?.role

    when (role) {
        UserRole.MODERATOR -> ModeratorSplash()
        UserRole.PARTICIPANT -> ParticipantSplash()
        null -> DefaultSplash()
    }
}

@Composable
fun DefaultSplash(isWhiteBackground: Boolean = false) {
    val colorStops = listOf(
        0.5f to Color(0xFF412962),
        1f to Color(0xFF73483A),
        1f to Color(0xFF9A5D63),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY),
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isWhiteBackground) {
                        Modifier.background(Color.White)
                    } else {
                        Modifier.background(backgroundBrush)
                    },
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.gathr_logo),
                contentDescription = "Pulsing App Logo",
                modifier = Modifier
                    .size(190.dp)
                    .scale(scale),
            )

            val logo = if (isWhiteBackground) {
                R.drawable.gathr_text
            } else {
                R.drawable.gathr_text_white
            }

            Image(
                painter = painterResource(id = logo),
                contentDescription = "Brand Name",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppMisc.screenBottomPadding),
            )
        }
    }
}

@Composable
fun ParticipantSplash() {
    DefaultSplash(isWhiteBackground = true)
}

@Composable
fun ModeratorSplash(modifier: Modifier = Modifier) {
    val colorStops = listOf(
        0f to Color(0xFF312245),
        1f to Color(0xFF7954AB),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY),
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.gathr_logo),
                contentDescription = "Pulsing App Logo",
                modifier = Modifier
                    .size(190.dp)
                    .scale(scale),
            )

            Text(
                text = "Moderator",
                style = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppMisc.screenBottomPadding)
                    .padding(bottom = 35.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.gathr_text_white),
                contentDescription = "Brand Name",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppMisc.screenBottomPadding),
            )
        }
    }
}


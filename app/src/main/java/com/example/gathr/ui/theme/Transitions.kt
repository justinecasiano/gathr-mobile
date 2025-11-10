package com.example.gathr.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

const val DEFAULT_TRANSITION_DURATION = 500

fun fadeIn(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition =
    fadeIn(animationSpec = tween(durationMillis))

fun fadeOut(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition =
    fadeOut(animationSpec = tween(durationMillis))

fun slideInRight(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition = slideInHorizontally(
    initialOffsetX = { 1000 },
    animationSpec = tween(durationMillis),
)

fun slideOutLeft(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition = slideOutHorizontally(
    targetOffsetX = { -1000 },
    animationSpec = tween(durationMillis),
)

fun slideInLeft(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition = slideInHorizontally(
    initialOffsetX = { -1000 },
    animationSpec = tween(durationMillis),
)

fun slideOutRight(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition = slideOutHorizontally(
    targetOffsetX = { 1000 },
    animationSpec = tween(durationMillis),
)

fun slideInUp(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis),
)

fun slideOutUp(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> -fullHeight },
    animationSpec = tween(durationMillis),
)

fun slideInDown(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> -fullHeight },
    animationSpec = tween(durationMillis),
)

fun slideOutDown(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(durationMillis),
)

fun scaleIn(durationMillis: Int = DEFAULT_TRANSITION_DURATION): EnterTransition = scaleIn(
    animationSpec = tween(durationMillis),
    initialScale = 0.8f, // Start slightly smaller
)

fun scaleOut(durationMillis: Int = DEFAULT_TRANSITION_DURATION): ExitTransition = scaleOut(
    animationSpec = tween(durationMillis),
    targetScale = 0.8f, // End slightly smaller
)

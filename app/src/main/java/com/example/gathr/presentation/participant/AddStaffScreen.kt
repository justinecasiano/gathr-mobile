package com.example.gathr.presentation.participant

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ClearTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toTitleCase
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.example.gathr.presentation.main.MainEffect

@Composable
fun AddStaffScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.userEffect.collect { effect ->
            when (effect) {
                UserEffect.NavigateNext -> onNavigateNext()
                UserEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AddStaffContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )
        if (state.isLoading) {
            LoadingOverlay()
        }

        when {
            state.actionError.contains("Remove", ignoreCase = true) -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionErrorChanged(""))
                        viewModel.handleIntent(UserIntent.ActionOnConfirmClicked {})
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }

            state.actionError.contains("You have not added", ignoreCase = true) -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionErrorChanged(""))
                        viewModel.handleIntent(UserIntent.ActionOnConfirmClicked {})
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }

            state.actionError.contains("Please wait") -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Success" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = {
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                        viewModel.handleIntent(UserIntent.FetchEvents)
                        viewModel.sendMainEffect(MainEffect.NavigateParticipantViewEvent)
                    },
                )
            }

            state.actionError.isNotBlank() -> {
                Alert(
                    title = "Error",
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddStaffContent(
    state: UserState, onIntent: (UserIntent) -> Unit
) {
    val hasFilled = state.searchStaff.isNotBlank()
    var added by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(UserIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (state.isUpdateEvent)
                            TextButton(
                                onClick = { onIntent(UserIntent.UpdateEvent) },
                                enabled = false,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Black,
                                    disabledContentColor = Color.Black.copy(0.5f)
                                )
                            ) {
                                Text(
                                    "Save",
                                    style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                            }
                    }
                )
            },
        ) { paddingValues ->
            Column(Modifier.fillMaxHeight()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.84f)
                        .background(Color(0xFFF0F0F0))
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                            end = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        )
                        .padding(horizontal = 30.dp)
                ) {
                    Text(
                        if (state.isUpdateEvent) "Update Staffs" else "Add Staffs",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            color = Color.Black
                        ),
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        ClearTextField(
                            modifier = Modifier.weight(9f),
                            text = state.searchStaff,
                            isError = if (!added) null else state.searchStaffError.isNotBlank(),
                            supportingText = state.searchStaffError,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Unspecified,
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Unspecified
                            ),
                            onValueChange = {
                                added = false
                                onIntent(UserIntent.SearchStaffChanged(it))
                                onIntent(UserIntent.SearchStaffErrorChanged(""))
                            },
                            onClick = {
                                added = false
                                onIntent(UserIntent.SearchStaffChanged(""))
                                onIntent(UserIntent.SearchStaffErrorChanged(""))
                            },
                            labelText = "Input email or username",
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            outlineColor = Color(0xFF777777),
                            iconColor = Color(0xFF3C3C3C)
                        )
                        Spacer(Modifier.width(15.dp))
                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF7B55A3),
                                            Color(0xFF583181)
                                        )
                                    ), shape = CircleShape
                                )
                                .padding(12.dp)
                                .clickable(enabled = hasFilled, onClick = {
                                    added = true
                                    onIntent(UserIntent.IsLoadingChanged(true))
                                    onIntent(UserIntent.ValidateAddStaff)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(R.drawable.add_icon),
                                contentDescription = "Add participant as staff",
                                tint = Color(0xFFF6F6F6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    val labelStyle = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )

                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        items(state.addStaffs, key = { staff -> staff.userId!! }) { staff ->
                            StaffRow(
                                firstName = staff.firstName, lastName = staff.lastName,
                                onRemove = {
                                    onIntent(UserIntent.ActionTitleChanged("Remove staff"))
                                    onIntent(UserIntent.ActionErrorChanged("Are you sure you want to remove ${staff.firstName.toTitleCase()} ${staff.lastName.toTitleCase()} as a staff?"))
                                    onIntent(
                                        UserIntent.ActionOnConfirmClicked {
                                            val updatedList =
                                                state.addStaffs.filter { it.userId != staff.userId }
                                            onIntent(UserIntent.AddStaffsChanged(updatedList))
                                        },
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(Modifier.height(40.dp))
                        }
                    }
                }
                Column(Modifier.background(Color.White)) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .border(2.dp, color = Color(0xFFD7D7D7))
                            .padding(horizontal = 55.dp)
                            .padding(top = 20.dp)
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                        contentAlignment = Alignment.Center
                    ) {
                        ElevatedButton(
                            text = if (state.isUpdateEvent) "DONE" else "CREATE EVENT",
                            onClick = {
                                if (state.addStaffs.count() == 0) {
                                    onIntent(UserIntent.ActionTitleChanged("No Staff"))
                                    onIntent(UserIntent.ActionErrorChanged("You have not added any staff, continue create event?"))
                                    onIntent(UserIntent.ActionOnConfirmClicked {
                                        onIntent(UserIntent.IsLoadingChanged(true))
                                        onIntent(UserIntent.DoCreateEvent)
                                    })
                                } else {
                                    onIntent(UserIntent.IsLoadingChanged(true))
                                    onIntent(UserIntent.DoCreateEvent)
                                }
                            },
                            isEnabled = state.actionError.isBlank(),
                            buttonColor = Color(0xFF7B55A3),
                            outlineColor = Color(0xFF4C2576),
                            textStyle = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                            isContrast = false,
                            buttonShape = RoundedCornerShape(20.dp),
                            shouldAddShadow = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaffRow(
    firstName: String, lastName: String, onRemove: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.profile),
            contentDescription = "Profile",
            modifier = Modifier.size(63.dp)
        )
        Spacer(Modifier.width(20.dp))
        Text(
            "${firstName.toTitleCase()} ${lastName.toTitleCase()}",
            style = TextStyle(
                fontFamily = AppFonts.rethinkSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = Color.Black.copy(0.8f)
            ),
            modifier = Modifier.weight(7f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.weight(1f))
        Box(Modifier.clickable(onClick = { onRemove() })) {
            Icon(
                painterResource(R.drawable.delete),
                contentDescription = "Remove Staff",
                tint = Color(0xFFEE101A),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview
@Composable
private fun StaffRowPreview() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        StaffRow("Angela Mae", "Cabrera") { }
    }
}
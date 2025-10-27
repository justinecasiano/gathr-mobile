package com.example.gathr.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R

object AppColors {
    val primary = Color(0xFF7B55A3)
    val primaryDark = Color(0xFF4C2576)
    val secondary = Color(0xFFF6835E)
    val secondaryDark = Color(0xFFD96944)

    val success = Color(0xFF94B983)
    val error = Color(0xFF820006)
}

object AppFonts {
    val rethinkSans = FontFamily(Font(R.font.rethink_sans_regular))
    val instrumentSans = FontFamily(Font(R.font.instrument_sans_regular))
}

object AppTextStyles {
    val button = TextStyle(
        fontFamily = AppFonts.instrumentSans,
        fontWeight = FontWeight.W700,
        fontSize = 40.sp,
        color = Color.Black
    )
}

object AppMisc {
    val screenBottomPadding = 30.dp
}
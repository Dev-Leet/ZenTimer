package com.example.zenfocus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

// Since we can't easily download fonts in this environment without google-services/internet access guarantee, 
// we will start by using standard System Fonts which map well.
// If the user wants specific assets later, we can add them.

val ModernFont = FontFamily.SansSerif
val DigitalFont = FontFamily.Monospace
val ClassicFont = FontFamily.Serif

// Default Material 3 Typography
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

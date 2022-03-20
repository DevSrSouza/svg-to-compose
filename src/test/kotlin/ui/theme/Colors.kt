package br.com.devsrsouza.svg2compose.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color


val darkColors by lazy {
    darkColors(
        background = Color(0xFF1A091F),
        onBackground = Color(0xFFC099CD),
        surface = Color(0xFF1B0036),// Color(0xFF5200A4),
        onSurface = Color(0xFFF9E3FF),
        primary = Color(0xFF0A010D),
        onPrimary = Color(0xFFF9E3FF),
        primaryVariant = Color(0xFF1B0036),
        secondary = Color(0xFF9F0037),
        onSecondary = Color(0xFFDCB5C2),
        secondaryVariant = Color(0xFF000000),
        error = Color(0xFFFF0059),
        onError = Color(0xFFDCB5C2)
    )
}

val lightColors by lazy {
    lightColors(
        background = Color(0xFFF9E3FF),
        onBackground = Color(0xFF1B0036),
        surface = Color(0xFF004848), // Color(0xFF5200A4),
        onSurface = Color(0xFFF9E3FF),
        primary = Color(0xFFC099CD),
        onPrimary = Color(0xFF1A091F),
        primaryVariant = Color(0xFF1B0036),
        secondary = Color(0xFF9F0037),
        onSecondary = Color(0xFFDCB5C2),
        secondaryVariant = Color(0xFF000000),
        error = Color(0xFFFF0059),
        onError = Color(0xFFDCB5C2)
    )
}
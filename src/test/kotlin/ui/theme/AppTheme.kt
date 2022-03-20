package br.com.devsrsouza.svg2compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

val isDarkMode = mutableStateOf(false)

@Composable
fun AppTheme(isDarkMode: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (isDarkMode) darkColors else lightColors

    val typography = if (isDarkMode) darkTypography else lightTypography

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )

}



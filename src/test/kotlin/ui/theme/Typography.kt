package br.com.devsrsouza.svg2compose.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable

private var dark: Typography? = null
private var light: Typography? = null

val darkTypography: Typography
    @Composable get() = dark ?: MaterialTheme.typography.copy(

    ).also {
        dark = it
    }

val lightTypography
    @Composable get() = light ?: MaterialTheme.typography.copy(

    ).also {
        light = it
    }
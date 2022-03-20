package br.com.devsrsouza.svg2compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.compose.icons.EvaIcons
import br.com.compose.icons.evaicons.*


fun main() = application {
    Window(::exitApplication, rememberWindowState(width = 1020.dp, height = 800.dp)) {

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
            Column {
                Row {
                    Icon(EvaIcons.Branch12, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale12, "Icon of size 12")
                }

                Row {
                    Icon(EvaIcons.Branch14, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale14, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch16, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale16, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch18, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale18, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch24, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale24, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch32, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale32, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch64, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale64, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch128, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale128, "Icon of size 12")
                }
                Row {
                    Icon(EvaIcons.Branch640, "Icon of size 12")
                    Icon(EvaIcons.IcBalanceScale640, "Icon of size 12")
                }
            }
        }

    }
}

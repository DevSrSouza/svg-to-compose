package br.com.devsrsouza.svg2compose

import java.io.File

fun main() {
    val iconTest = File("raw-icons")
    val src = File("build/generated-icons")

    Svg2Compose.parse(
        "br.com.devsrsouza.myfuckingapp.icons",
        "Linea",
        src,
        iconTest,
        iconNameTransformer = { it.removePrefix("Weather") }
    )
}
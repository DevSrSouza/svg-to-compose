package br.com.devsrsouza.svg2compose

import java.io.File

fun main() {
    val iconTest = File("raw-icons")
    val svgFiles = iconTest.listFiles().filter { it.extension == "svg" }
    val src = File("build/generated-icons")

    Svg2Compose.parse(
        "br.com.devsrsouza.myfuckingapp.icons",
        "Linea/Weather",
        src,
        svgFiles,
        iconNameTransformer = { it.removePrefix("Weather") }
    )
}
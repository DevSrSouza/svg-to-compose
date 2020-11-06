package br.com.devsrsouza.svg2compose

import androidx.compose.material.icons.generator.*
import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File

fun main() {
    val iconTest = File("raw-icons/weather_aquarius.svg")
    val i = File("drawable-icons/weather_aquarius.xml")
    val output = i.outputStream()
    Svg2Vector.parseSvgToXml(iconTest, output)

    val icon = Icon("weather_aquarius", "weather_aquarius", IconTheme.Outlined, i.readText())

    val writer = IconWriter(listOf(icon))
    writer.generateTo(File("build/icons-generated")) { true }
}
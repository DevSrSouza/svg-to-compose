package br.com.devsrsouza.svg2compose

import java.io.File

fun main() {
    val iconTest = File("raw-icons4")
    val src = File("build/generated-icons").apply { mkdirs()}

    Svg2Compose.parse(
        "br.com.compose.icons",
        "EvaIcons",
        src,
        iconTest,
        iconNameTransformer = { name, group -> name.removeSuffix(group, ignoreCase = true) }
    )
}

private fun String.removeSuffix(suffix: String, ignoreCase: Boolean): String {
    if(ignoreCase) {
        val index = lastIndexOf(suffix, ignoreCase = true)

        return if(index > -1) substring(0, index) else this
    } else {
        return removeSuffix(suffix)
    }
}
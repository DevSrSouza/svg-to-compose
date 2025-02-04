package br.com.devsrsouza.svg2compose

import java.io.File
import java.util.Locale


fun main() {
    val iconTest = File("dist/icons/svg")
    val src = File("build/generated-icons").apply { mkdirs() }

    Svg2Compose.parse(
        applicationIconPackage = "com.test",
        accessorName = "Icons",
        outputSourceDirectory = src,
        vectorsDirectory = iconTest,
        type = VectorType.SVG,
        iconNameTransformer = { name, group ->
            name.removePrefix(group)
                .toUpperCamelCase()
        },
        allAssetsPropertyName = "AllIcons",
        generatePreview = true,
        autoMirrorRule = { name ->
            name.contains("forward", ignoreCase = true) ||
                    name.contains("backward", ignoreCase = true)
        }
    )
}

fun String.toUpperCamelCase(): String {
    return this.split("[-_. ]".toRegex())
        .joinToString("") { part ->
            part.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        }
}

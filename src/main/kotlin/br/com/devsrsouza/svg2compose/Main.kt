package br.com.devsrsouza.svg2compose

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import java.nio.file.Paths
import java.util.*

fun main(vararg args: String) = Svg2ComposeCommand().main(args.toList())

private class Svg2ComposeCommand : CliktCommand(
    help = BuildConfig.DESCRIPTION,
) {
    private val inputVectorsPath by argument(
        "input-vectors-path",
        help = "The directory containing the SVG files to be converted."
    )
    private val outputSourcePath by argument(
        "output-source-path",
        help = "The output directory of the generated source code."
    )

    private val applicationIconPackage by stringOption(
        "--application-icon-package",
        help = "Represents what will be the final package of the generated Vector Source.",
        default = "com.example"
    )
    private val accessorName by stringOption(
        "--accessor-name",
        help = "The name will be usage to access the Vector in the code like `MyIconPack.IconName` or `MyIconPack.IconGroupDir.IconName`",
        default = "Icons"
    )
    private val allAssetsPropertyName by stringOption(
        "--all-assets-property-name",
        help = "The name of the property that will contain all assets.",
        default = "AllIcons"
    )
    private val generatePreview by generatePreview()
    private val vectorType by vectorType()

    init {
        versionOption(BuildConfig.VERSION_NAME, names = setOf("-v", "--version"))
    }

    override fun run() {
        Svg2Compose.parse(
            applicationIconPackage = applicationIconPackage,
            accessorName = accessorName,
            outputSourceDirectory = Paths.get(outputSourcePath).toFile(),
            vectorsDirectory = Paths.get(inputVectorsPath).toFile(),
            type = vectorType,
            allAssetsPropertyName = allAssetsPropertyName,
            generatePreview = generatePreview
        )
    }

    private fun ParameterHolder.stringOption(
        vararg names: String,
        help: String,
        default: String
    ): OptionWithValues<String, String, String> {
        return option(names = names, help = help).default(default)
    }

    private fun ParameterHolder.generatePreview(): OptionWithValues<Boolean, Boolean, Boolean> {
        return option(
            "--generate-preview",
            help = "Whether to generate @Preview of the icons. Use 'true' or 'false'."
        ).convert { arg ->
            when (arg.lowercase(Locale.US)) {
                "true" -> true
                "false" -> false
                else -> throw IllegalArgumentException("Invalid value for --generate-preview. Use 'true' or 'false'.")
            }
        }.default(true)
    }

    private fun ParameterHolder.vectorType(): OptionWithValues<VectorType, VectorType, VectorType> {
        return option("--vector-type", help = "The type of the vector to be generated. Use 'svg' or 'drawable'.")
            .convert { arg ->
                when (arg.lowercase(Locale.US)) {
                    "svg" -> VectorType.SVG
                    "drawable" -> VectorType.DRAWABLE
                    else -> throw IllegalArgumentException("Invalid value for --vector-type. Use 'svg' or 'drawable'.")
                }
            }
            .default(VectorType.SVG)
    }
}
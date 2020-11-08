package br.com.devsrsouza.svg2compose

import androidx.compose.material.icons.generator.Icon
import androidx.compose.material.icons.generator.IconWriter
import androidx.compose.material.icons.generator.toKotlinPropertyName
import com.android.ide.common.vectordrawable.Svg2Vector
import com.google.common.base.CaseFormat
import java.io.File

object Svg2Compose {

    /**
     * Generates source code for the [vectors] files.
     *
     * Supported types: SVG, Android Vector Drawable XML
     *
     * @param applicationIconPackage Represents what will be the final package of the generated Vector Source. ex com.yourcompany.yourapplication.icons
     * @param iconGroup Represents the Icon call chain on the code: Group1.Group2.IconFileName = "Group1/Group2". separated by `/` .
     */
    fun parse(
        applicationIconPackage: String,
        iconGroup: String,
        outputSourceDirectory: File,
        vectors: List<File>,
        type: VectorType = VectorType.SVG,
        iconNameTransformer: (String) -> String = { it }
    ) {
        val drawableDir = drawableTempDirectory()

        val drawables: List<File> = when(type) {
            VectorType.SVG -> vectors.map {
                val parsedFile = File(drawableDir, "${it.nameWithoutExtension}.xml")
                Svg2Vector.parseSvgToXml(it, parsedFile.outputStream())

                parsedFile
            }
            VectorType.DRAWABLE -> vectors
        }

        val icons = drawables.map {
            val name = it.nameWithoutExtension
            Icon(name.toKotlinPropertyName(), name, it.readText())
        }

        val writer = IconWriter(
            applicationIconPackage,
            iconGroup,
            icons,
            iconNameTransformer
        )

        writer.generateTo(outputSourceDirectory) { true }
    }

    private fun drawableTempDirectory() = createTempDir(suffix = "svg2compose/")

}
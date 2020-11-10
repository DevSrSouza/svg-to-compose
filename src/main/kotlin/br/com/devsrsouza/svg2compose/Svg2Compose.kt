package br.com.devsrsouza.svg2compose

import androidx.compose.material.icons.generator.Icon
import androidx.compose.material.icons.generator.IconGroup
import androidx.compose.material.icons.generator.IconWriter
import androidx.compose.material.icons.generator.toKotlinPropertyName
import com.android.ide.common.vectordrawable.Svg2Vector
import java.io.File

typealias IconNameTransformer = (iconName: String, group: String) -> String

object Svg2Compose {

    /**
     * Generates source code for the [vectors] files.
     *
     * Supported types: SVG, Android Vector Drawable XML
     *
     * @param applicationIconPackage Represents what will be the final package of the generated Vector Source. ex com.yourcompany.yourapplication.icons
     * @param accessorName will be usage to access the Vector in the code like `MyIconPack.IconName` or `MyIconPack.IconGroupDir.IconName`
     */
    fun parse(
        applicationIconPackage: String,
        accessorName: String,
        outputSourceDirectory: File,
        vectorsDirectory: File,
        type: VectorType = VectorType.SVG,
        iconNameTransformer: IconNameTransformer = { it, _ -> it },
        allAssetsPropertyName: String = "AllAssets"
    ) {
        val drawableDir = drawableTempDirectory()
        val depthFiles = vectorsDirectory.walkTopDown()
            .maxDepth(8)
            .filter { !it.isDirectory }
            .filter { it.extension.equals(type.extension, ignoreCase = true) }

        fun nameRelative(vectorFile: File) = vectorFile.relativeTo(vectorsDirectory).path

        val drawables: List<Pair<IconGroup, File>> = when(type) {
            VectorType.SVG -> depthFiles.map {
                val iconName = nameRelative(it).withoutExtension

                val parsedFile = File(drawableDir, "${iconName}.xml")
                parsedFile.parentFile.mkdirs()

                Svg2Vector.parseSvgToXml(it, parsedFile.outputStream())

                "${accessorName}/$iconName" to parsedFile
            }.toList()
            VectorType.DRAWABLE -> depthFiles
                .map {
                    val iconName = nameRelative(it).withoutExtension

                    "${accessorName}/$iconName" to it
                }.toList()
        }

        val icons = drawables.map {
            val groups = it.first.split("/")
            groups.take(groups.size-1).map { it.toKotlinPropertyName() }.joinToString("/") to Icon(
                groups.last().toKotlinPropertyName(),
                groups.last(),
                it.second.readText()
            )
        }

        val writer = IconWriter(
            applicationIconPackage,
            icons,
            iconNameTransformer,
            allAssetsPropertyName
        )

        writer.generateTo(outputSourceDirectory) { true }
    }

    private fun drawableTempDirectory() = createTempDir(suffix = "svg2compose/")

    private val String.withoutExtension get() = substringBeforeLast(".")
}
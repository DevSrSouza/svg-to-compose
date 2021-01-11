package br.com.devsrsouza.svg2compose

import androidx.compose.material.icons.generator.*
import androidx.compose.material.icons.generator.toKotlinPropertyName
import com.android.ide.common.vectordrawable.Svg2Vector
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import java.io.File
import java.util.*

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
        fun nameRelative(vectorFile: File) = vectorFile.relativeTo(vectorsDirectory).path

        val drawableDir = drawableTempDirectory()

        val groupStack = Stack<GeneratedGroup>()

        vectorsDirectory.walkTopDown()
            .maxDepth(10)
            .onEnter {
                println(it)
                val dirIcons = it.listFiles()
                    .filter { !it.isDirectory }
                    .filter { it.extension.equals(type.extension, ignoreCase = true) }

                val previousGroup = groupStack.peekOrNull()

                // if there is no previous group, this is the root dir, and the group name should be the accessorName
                val groupName = if(previousGroup == null) accessorName else it.name.toKotlinPropertyName()
                val groupPackageName = groupName.toLowerCase()
                val groupPackage = previousGroup?.groupPackage?.let { groupPkg -> "$groupPkg.${groupPackageName}" }
                    ?: "$applicationIconPackage.${groupPackageName}"

                val (groupFileSpec, groupClassName) = IconGroupGenerator(
                    groupPackage,
                    groupName
                ).createFileSpec(previousGroup?.groupClass)


                val generatedIconsMemberNames = if(dirIcons.isNotEmpty()) {
                    val drawables: List<File> = when (type) {
                        VectorType.SVG -> dirIcons.map {
                            val iconName = nameRelative(it).withoutExtension

                            val parsedFile = File(drawableDir, "${iconName}.xml")
                            parsedFile.parentFile.mkdirs()

                            Svg2Vector.parseSvgToXml(it, parsedFile.outputStream())

                            parsedFile
                        }.toList()
                        VectorType.DRAWABLE -> dirIcons.toList()
                    }

                    val icons = drawables.map {
                        Icon(
                            it.nameWithoutExtension.toKotlinPropertyName(),
                            it.name,
                            it.readText()
                        )
                    }

                    val writer = IconWriter(
                        icons,
                        groupClassName,
                        groupPackage,
                        groupName,
                        iconNameTransformer
                    )

                    writer.generateTo(outputSourceDirectory) { true }
                } else {
                    emptyList<MemberName>()
                }

                val result = GeneratedGroup(
                    groupPackage,
                    groupName,
                    generatedIconsMemberNames,
                    groupClassName,
                    groupFileSpec,
                    childGroups = emptyList()
                )

                if(previousGroup != null) {
                    groupStack.pop()
                    groupStack.push(previousGroup.copy(childGroups = previousGroup.childGroups + result))
                }

                groupStack.push(result)

                true
            }
            .onLeave {
                val group = groupStack.pop()

                val allAssetsGenerator = AllIconAccessorGenerator(
                    group.generatedIconsMemberNames,
                    group.groupClass,
                    allAssetsPropertyName,
                    group.childGroups
                )

                for (propertySpec in allAssetsGenerator.createPropertySpec(group.groupFileSpec)) {
                    group.groupFileSpec.addProperty(propertySpec)
                }

                group.groupFileSpec.build().writeTo(outputSourceDirectory)
            }
            .toList() // consume, to onEnter and onLeave be triggered
    }

    private fun drawableTempDirectory() = createTempDir(suffix = "svg2compose/")

    private val String.withoutExtension get() = substringBeforeLast(".")
}

data class GeneratedGroup(
    val groupPackage: String,
    val groupName: String,
    val generatedIconsMemberNames: List<MemberName>,
    val groupClass: ClassName,
    val groupFileSpec: FileSpec.Builder,
    val childGroups: List<GeneratedGroup>
)
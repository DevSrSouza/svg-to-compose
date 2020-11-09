package androidx.compose.material.icons.generator

import com.squareup.kotlinpoet.*
import java.io.File

data class IconGroupGenerationResult(
    val firstGroup: ClassName,
    val lastGroup: ClassName,
    val firstGroupFileSpec: FileSpec.Builder,
    val fileSpecs: List<FileSpec.Builder>
)

class IconGroupGenerator(
    private val applicationIconPackage: String,
    private val iconGroup: String,
) {
    // writeTo to source directory and return the last group for the Icons
    /*fun writeTo(
        outputSrcDirectory: File
    ): ClassName {
        val groups = createFileForGroups()
        for (groupFileSpec in groups.second) {
            groupFileSpec.writeTo(outputSrcDirectory)
        }

        return groups.first
    }*/

    // ClassName = Last Group
    fun createFileSpecsForGroups(): IconGroupGenerationResult {
        val groups = iconGroup.split("/")

        val firstGroup = createGroup(null, groups.first())

        if (groups.size > 1) {
            val subGroups = groups.slice(1 until groups.size).scan(firstGroup) { previous, group ->
                createGroup(previous.second, group)
            }

            return IconGroupGenerationResult(
                firstGroup.second,
                subGroups.last().second,
                firstGroup.first,
                subGroups.map { it.first } + firstGroup.first
            )
        } else {
            return IconGroupGenerationResult(
                firstGroup.second,
                firstGroup.second,
                firstGroup.first,
                listOf(firstGroup.first)
            )
        }
    }

    private fun createGroup(previousGroupObject: ClassName?, group: String): Pair<FileSpec.Builder, ClassName> {
        val iconGroupPackage = applicationIconPackage

        val objectName = "${group}${if(previousGroupObject != null) "Group" else ""}"

        val objectSpec = TypeSpec.objectBuilder(objectName).build()
        val createdObjectClassName = ClassName(iconGroupPackage, objectName)

        return FileSpec.builder(
            packageName = iconGroupPackage,
            fileName = group
        )
            .addType(objectSpec)
            .apply {
                if(previousGroupObject != null) {
                    addProperty(PropertySpec.builder(group, createdObjectClassName)
                        .receiver(previousGroupObject)
                        .getter(FunSpec.getterBuilder().addStatement("return $objectName").build())
                        .build()
                    )
                }
            } to createdObjectClassName
    }
}
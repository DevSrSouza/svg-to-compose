package androidx.compose.material.icons.generator

import com.squareup.kotlinpoet.*
import java.io.File

class IconGroupWriter(
    private val applicationIconPackage: String,
    private val iconGroup: String,
) {
    // writeTo to source directory and return the last group for the Icons
    fun writeTo(
        outputSrcDirectory: File
    ): ClassName {
        val groups = createFileForGroups()
        for (groupFileSpec in groups.second) {
            groupFileSpec.writeTo(outputSrcDirectory)
        }

        return groups.first
    }

    // ClassName = Last Group
    private fun createFileForGroups(): Pair<ClassName, List<FileSpec>> {
        val groups = iconGroup.split("/")

        val firstGroup = createGroup(null, groups.first())

        if (groups.size > 1) {
            val subGroups = groups.slice(1 until groups.size).scan(firstGroup) { previous, group ->
                createGroup(previous.second, group)
            }

            return subGroups.last().second to subGroups.map { it.first } + firstGroup.first
        } else {
            return firstGroup.second to listOf(firstGroup.first)
        }
    }

    private fun createGroup(previousGroupObject: ClassName?, group: String): Pair<FileSpec, ClassName> {
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
            }
            .build() to createdObjectClassName
    }
}
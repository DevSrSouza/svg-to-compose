/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material.icons.generator

import androidx.compose.material.icons.generator.util.backingPropertySpec
import androidx.compose.material.icons.generator.util.withBackingProperty
import androidx.compose.material.icons.generator.vector.Fill
import androidx.compose.material.icons.generator.vector.Vector
import androidx.compose.material.icons.generator.vector.VectorNode
import com.squareup.kotlinpoet.*
import java.util.Locale

data class VectorAssetGenerationResult(
    val sourceGeneration: FileSpec, val accessProperty: String
)

/**
 * Generator for creating a Kotlin source file with a VectorAsset property for the given [vector],
 * with name [iconName] and theme [iconTheme].
 *
 * @param iconName the name for the generated property, which is also used for the generated file.
 * I.e if the name is `Menu`, the property will be `Menu` (inside a theme receiver object) and
 * the file will be `Menu.kt` (under the theme package name).
 * @param iconTheme the theme that this vector belongs to. Used to scope the property to the
 * correct receiver object, and also for the package name of the generated file.
 * @param vector the parsed vector to generate VectorAssetBuilder commands for
 */
class VectorAssetGenerator(
    private val iconName: String,
    private val iconGroupPackage: String,
    private val vector: Vector
) {
    /**
     * @return a [FileSpec] representing a Kotlin source file containing the property for this
     * programmatic [vector] representation.
     *
     * The package name and hence file location of the generated file is:
     * [PackageNames.MaterialIconsPackage] + [IconTheme.themePackageName].
     */
    fun createFileSpec(groupClassName: ClassName): VectorAssetGenerationResult {
        // Use a unique property name for the private backing property. This is because (as of
        // Kotlin 1.4) each property with the same name will be considered as a possible candidate
        // for resolution, regardless of the access modifier, so by using unique names we reduce
        // the size from ~6000 to 1, and speed up compilation time for these icons.
        @OptIn(ExperimentalStdlibApi::class)
        val backingPropertyName = "_" + iconName.decapitalize(Locale.ROOT)
        val backingProperty = backingPropertySpec(name = backingPropertyName, ClassNames.ImageVector)

        val generation = FileSpec.builder(
            packageName = iconGroupPackage,
            fileName = iconName
        ).addProperty(
            PropertySpec.builder(name = iconName, type = ClassNames.ImageVector)
                .receiver(groupClassName)
                .getter(iconGetter(backingProperty))
                .build()
        ).addProperty(
            backingProperty
        ).setIndent().build()

        return VectorAssetGenerationResult(generation, iconName)
    }

    /**
     * @return the body of the getter for the icon property. This getter returns the backing
     * property if it is not null, otherwise creates the icon and 'caches' it in the backing
     * property, and then returns the backing property.
     */
    private fun iconGetter(backingProperty: PropertySpec): FunSpec {

        val parameterList = with(vector) {
            listOfNotNull(
                "name = \"${iconName}\"",
                "defaultWidth = ${width.withMemberIfNotNull}",
                "defaultHeight = ${height.withMemberIfNotNull}",
                "viewportWidth = ${viewportWidth}f",
                "viewportHeight = ${viewportHeight}f"
            )
        }

        val parameters = parameterList.joinToString(prefix = "(", postfix = ")")

        val members: Array<Any> = listOfNotNull(
            MemberNames.ImageVectorBuilder,
            vector.width.memberName,
            vector.height.memberName
        ).toTypedArray()

        return FunSpec.getterBuilder()
            .withBackingProperty(backingProperty) {
                addCode(buildCodeBlock {
                    beginControlFlow(
                        "%N = %M$parameters.apply",
                        backingProperty,
                        *members
                    )
                    vector.nodes.forEach { node -> addRecursively(node) }
                    endControlFlow()
                    addStatement(".build()")
                })
            }
            .build()
    }


}

/**
 * Recursively adds function calls to construct the given [vectorNode] and its children.
 */
private fun CodeBlock.Builder.addRecursively(vectorNode: VectorNode) {
    when (vectorNode) {
        // TODO: b/147418351 - add clip-paths once they are supported
        is VectorNode.Group -> {
            beginControlFlow("%M", MemberNames.Group)
            vectorNode.paths.forEach { path ->
                addRecursively(path)
            }
            endControlFlow()
        }
        is VectorNode.Path -> {
            addPath(vectorNode) {
                vectorNode.nodes.forEach { pathNode ->
                    addStatement(pathNode.asFunctionCall())
                }
            }
        }
    }
}

/**
 * Adds a function call to create the given [path], with [pathBody] containing the commands for
 * the path.
 */
private fun CodeBlock.Builder.addPath(
    path: VectorNode.Path,
    pathBody: CodeBlock.Builder.() -> Unit
) {
    val hasFillColor = path.fill is Fill.FillColor
    val fillColorHex = (path.fill as? Fill.FillColor)?.colorHex
    val hasStrokeColor = path.strokeColorHex != null

    val parameterList = with(path) {
        listOfNotNull(
            "fill = ${if(hasFillColor) "%M(%M(0x$fillColorHex))" else "null"}",
            "stroke = ${if(hasStrokeColor) "%M(%M(0x$strokeColorHex))" else "null"}",
            "fillAlpha = ${fillAlpha}f".takeIf { fillAlpha != 1f },
            "strokeAlpha = ${strokeAlpha}f".takeIf { strokeAlpha != 1f },
            "strokeLineWidth = ${strokeLineWidth.withMemberIfNotNull}",
            "strokeLineCap = %M",
            "strokeLineJoin = %M",
            "strokeLineMiter = ${strokeLineMiter}f",
            "pathFillType = %M"
        )
    }

    val parameters = parameterList.joinToString(prefix = "(", postfix = ")")

    val members: Array<Any> = listOfNotNull(
        MemberNames.Path,
        MemberNames.SolidColor.takeIf { hasFillColor },
        MemberNames.Color.takeIf { hasFillColor },
        MemberNames.SolidColor.takeIf { hasStrokeColor },
        MemberNames.Color.takeIf { hasStrokeColor },
        path.strokeLineWidth.memberName,
        path.strokeLineCap.memberName,
        path.strokeLineJoin.memberName,
        path.fillType.memberName
    ).toTypedArray()

    beginControlFlow(
        "%M$parameters",
       *members
    )

    pathBody()
    endControlFlow()
}

private val GraphicUnit.withMemberIfNotNull: String get() = "${value}${if (memberName != null) ".%M" else "f"}"
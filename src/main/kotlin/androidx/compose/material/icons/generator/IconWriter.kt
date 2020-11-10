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

import br.com.devsrsouza.svg2compose.IconNameTransformer
import com.squareup.kotlinpoet.MemberName
import java.io.File

typealias IconGroup = String

/**
 * Generates programmatic representation of all [icons] using [VectorAssetGenerator].
 *
 * @property icons the list of [Icon]s to generate Kotlin files for
 */
class IconWriter(
    private val applicationIconPackage: String,
    private val icons: List<Pair<IconGroup, Icon>>,
    private val iconNameTransformer: IconNameTransformer
) {
    /**
     * Generates icons and writes them to [outputSrcDirectory], using [iconNamePredicate] to
     * filter what icons to generate for.
     *
     * @param outputSrcDirectory the directory to generate source files in
     * @param iconNamePredicate the predicate that filters what icons should be generated. If
     * false, the icon will not be parsed and generated in [outputSrcDirectory].
     */
    fun generateTo(
        outputSrcDirectory: File,
        iconNamePredicate: (String) -> Boolean
    ) {
        // generating objects related to iconGroup
        val groups = icons.map { it.first }.distinct()
            .associateWith {
                val groupWriter = IconGroupGenerator(applicationIconPackage, it)
                val result = groupWriter.createFileSpecsForGroups()

                result
            }

        val generatedIconsProperties = icons.filter { (group, icon) ->
            val iconName = icon.kotlinName.trim()

            iconNamePredicate(iconName)
        }.map { (group, icon) ->
            val iconName = icon.kotlinName.trim()

            val vector = IconParser(icon).parse()

            val (fileSpec, accessProperty) = VectorAssetGenerator(
                applicationIconPackage,
                iconNameTransformer(iconName, group),
                group,
                vector
            ).createFileSpec(groups[group]!!.lastGroup)

            fileSpec.writeTo(outputSrcDirectory)

            MemberName(fileSpec.packageName, accessProperty)
        }

        val groupResult = groups.values.first()
        val accessorGroupFileSpec = groupResult.firstGroupFileSpec
        val accessorGroupMember = groupResult.firstGroup

        val allIconAccessor = AllIconAccessorGenerator(
            generatedIconsProperties,
            accessorGroupMember
        )

        for (propertySpec in allIconAccessor.createPropertySpec(accessorGroupFileSpec)) {
            accessorGroupFileSpec.addProperty(propertySpec)
        }

        for (fileSpec in groupResult.fileSpecs) {
            fileSpec.build().writeTo(outputSrcDirectory)
        }
    }
}

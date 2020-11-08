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

import java.io.File

/**
 * Generates programmatic representation of all [icons] using [VectorAssetGenerator].
 *
 * @property icons the list of [Icon]s to generate Kotlin files for
 */
class IconWriter(
    private val applicationIconPackage: String,
    private val iconGroup: String,
    private val icons: List<Icon>,
    private val iconNameTransformer: (String) -> String
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
        val groupWriter = IconGroupWriter(applicationIconPackage, iconGroup)
        val lastGroupClassName = groupWriter.writeTo(outputSrcDirectory)

        icons.forEach { icon ->
            val iconName = icon.kotlinName.trim()

            if (!iconNamePredicate(iconName)) return@forEach

            val vector = IconParser(icon).parse()

            val fileSpec = VectorAssetGenerator(
                applicationIconPackage,
                iconNameTransformer(iconName),
                iconGroup,
                vector
            ).createFileSpec(lastGroupClassName)

            fileSpec.writeTo(outputSrcDirectory)
        }
    }
}

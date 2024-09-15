package androidx.compose.material.icons.generator

import androidx.compose.material.icons.generator.util.backingPropertySpec
import androidx.compose.material.icons.generator.util.withBackingProperty
import br.com.devsrsouza.svg2compose.GeneratedGroup
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class AllIconAccessorGenerator(
    private val iconProperties: Collection<MemberName>,
    private val accessClass: ClassName,
    private val allAssetsPropertyName: String,
    private val childGroups: List<GeneratedGroup>,
    private val generateStringAccessor: Boolean,
) {
    fun createPropertySpec(
        fileSpec: FileSpec.Builder,
    ): List<PropertySpec> {
        val list = (List::class).asClassName()
        // preventing that a asset has the name List and conflict with Kotlin List import
        fileSpec.addAliasedImport(list, "____KtList")

        val allIconsType = list.parameterizedBy(ClassNames.ImageVector)
        val allIconsBackingProperty = backingPropertySpec("__$allAssetsPropertyName", allIconsType)

        // preventing import conflict when different groups has the same asset name.
        iconProperties.forEach { memberName ->
            if(memberName.simpleName == accessClass.simpleName) {
                // preventing import conflict when the asset name is the same name as the accessor
                fileSpec.addAliasedImport(memberName, "___${memberName.simpleName}")
            }
        }

        val allIconsParametersFromGroups = childGroups.map { "%M.${allAssetsPropertyName}" }

        // adding import to `AllAssets`
        childGroups.forEach {
            fileSpec.addImport(it.groupPackage, allAssetsPropertyName)
        }

        val allIconsParameters = iconProperties.map { "%M" }
        val parameters = allIconsParameters.joinToString(prefix = "(", postfix = ")")
        val childGroupsParameters = allIconsParametersFromGroups.joinToString(" + ")

        val allIconProperty = PropertySpec.builder(allAssetsPropertyName, allIconsType)
            .receiver(accessClass)
            .getter(FunSpec.getterBuilder().withBackingProperty(allIconsBackingProperty) {
                addStatement(
                    "%N= ${if(childGroups.isNotEmpty()) "$childGroupsParameters + " else ""}listOf$parameters",
                    allIconsBackingProperty,
                    *(childGroups.map(::groupAllIconsMember) + iconProperties).toTypedArray()
                )
            }.build())
            .build()

        return buildList {
            add(allIconsBackingProperty)
            add(allIconProperty)

            if (generateStringAccessor) {
                addAll(createPropertySpecForNamed(fileSpec))
            }
        }
    }

    private fun createPropertySpecForNamed(
        fileSpec: FileSpec.Builder,
    ): List<PropertySpec> {
        val map = (Map::class).asClassName()
        // preventing that a asset has the name List and conflict with Kotlin List import
        fileSpec.addAliasedImport(map, "____KtMap")

        val allAssetsNamedPropertyName = "${allAssetsPropertyName}Named"

        val allIconsType = map.parameterizedBy(STRING, ClassNames.ImageVector)
        val allIconsBackingProperty = backingPropertySpec("__$allAssetsNamedPropertyName", allIconsType)

        val allIconsParametersFromGroups = childGroups.map { "%M.$allAssetsNamedPropertyName.mapKeys { \"\${%M.groupName}.\${it.key}\"}" }

        // adding import to `AllAssetsNamed`
        childGroups.forEach {
            fileSpec.addImport(it.groupPackage, allAssetsNamedPropertyName)
            fileSpec.addImport(it.groupPackage, "groupName")
        }

        val allIconsParameters = iconProperties.map { "\"${it.simpleName.lowercase()}\" to %M" }
        val parameters = allIconsParameters.joinToString(prefix = "(", postfix = ")")
        val childGroupsParameters = allIconsParametersFromGroups.joinToString(" + ")

        val allIconProperty = PropertySpec.builder(allAssetsNamedPropertyName, allIconsType)
            .receiver(accessClass)
            .getter(FunSpec.getterBuilder().withBackingProperty(allIconsBackingProperty) {
                addStatement(
                    "%N= ${if(childGroups.isNotEmpty()) "$childGroupsParameters + " else ""}mapOf$parameters",
                    allIconsBackingProperty,
                    *(childGroups.flatMap{ listOf(groupAllIconsMember(it), groupAllIconsMember(it)) } + iconProperties).toTypedArray()
                )
            }.build())
            .build()

        return listOf(
            allIconsBackingProperty, allIconProperty
        )
    }

    private fun groupAllIconsMember(group: GeneratedGroup): MemberName {
        return MemberName(group.groupPackage, group.groupName.second)
    }
}
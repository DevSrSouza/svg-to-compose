package androidx.compose.material.icons.generator

import androidx.compose.material.icons.generator.util.backingPropertySpec
import androidx.compose.material.icons.generator.util.withBackingProperty
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class AllIconAccessorGenerator(
    private val iconProperties: List<MemberName>,
    private val accessClass: ClassName,
    private val allAssetsPropertyName: String
) {
    fun createPropertySpec(fileSpec: FileSpec.Builder): List<PropertySpec> {
        val list = (List::class).asClassName()
        fileSpec.addAliasedImport(list, "____KtList")

        val allIconsType = list.parameterizedBy(ClassNames.VectorAsset)
        val allIconsBackingProperty = backingPropertySpec("__$allAssetsPropertyName", allIconsType)

        iconProperties.find { it.simpleName == accessClass.simpleName }?.let {
            fileSpec.addAliasedImport(it, "___$it")
        }

        val allIconsParameters = iconProperties.map { "%M" }
        val parameters = allIconsParameters.joinToString(prefix = "(", postfix = ")")
        val allIconProperty = PropertySpec.builder(allAssetsPropertyName, allIconsType)
            .receiver(accessClass)
            .getter(FunSpec.getterBuilder().withBackingProperty(allIconsBackingProperty) {
                addStatement("%N= listOf$parameters", allIconsBackingProperty, *iconProperties.toTypedArray())
            }.build())
            .build()

        return listOf(
            allIconsBackingProperty, allIconProperty
        )
    }
}
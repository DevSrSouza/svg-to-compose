# SVG to Compose [Experimental]

Converts SVG or Android Vector Drawable to Compose code.

- [Android Studio/IntelliJ plugin](https://plugins.jetbrains.com/plugin/18619-svg-to-compose) by [overpass](https://github.com/overpas)

### Why this project

On the start of [Compose for Desktop](https://www.jetbrains.com/lp/compose/) it did not support SVG files nor Android Vector Drawables. This also complicates a migration of the app to Multiplatform (Desktop and Android).
Currently, it does support Android Vector Drawables in the Desktop allowing to share your vectors. 

### Use cases

Now Compose for Desktop supports Android Vector Drawables. This means that the first reason why the project was created does not apply anymore.
This however does not mean that it is not useful anymore.

Use cases:
- Manipulate dynamic an SVG file in code, you can generate and do source code modifications.
- Create an Icon pack similar to how Material Icons works on Compose ([compose-icons](https://github.com/DevSrSouza/compose-icons) is build with SVG to Compose)

### How its work

The project uses Android's [Svg2Vector](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java) to convert SVG to Android Drawables and uses a customized material icon code generator from the Jetpack Compose [source code](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/icons/generator/) to generate the source code of the SVG file.

### Example 1: Running with Kotlin Scripting

file: `jetnews-drawables-to-compose.main.kts`

```kotlin
@file:Repository("https://jitpack.io")
@file:Repository("https://maven.google.com")
@file:Repository("https://jetbrains.bintray.com/trove4j")

@file:DependsOn("com.github.DevSrSouza:svg-to-compose:-SNAPSHOT")
@file:DependsOn("com.google.guava:guava:23.0")
@file:DependsOn("com.android.tools:sdk-common:27.2.0-alpha16")
@file:DependsOn("com.android.tools:common:27.2.0-alpha16")
@file:DependsOn("com.squareup:kotlinpoet:1.7.2")
@file:DependsOn("org.ogce:xpp3:1.1.6")

import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import java.io.File

val assetsDir = File("assets")
val srcDir = File("src/main/kotlin")

Svg2Compose.parse(
    applicationIconPackage = "assets",
    accessorName = "JetnewsAssets",
    outputSourceDirectory = srcDir,
    vectorsDirectory = assetsDir,
    type = VectorType.DRAWABLE,
    allAssetsPropertyName = "AllAssets"
)
```

![](https://i.imgur.com/f7txCag.png)

Generating code by using executing `kotlin jetnews-drawables-to-compose`

![](https://i.imgur.com/5UTmT70.png)

**Using in code**: `JetnewsAssets.JetnewsLogo`

![](https://i.imgur.com/YAriDvV.png)

#### Svg

The only difference for SVG files is the `VectorType.SVG`.

### Example 2: Generating a whole Icon pack

For Icon Packs, subgroups is supported: `IconPack/group/icon.svg`

![](https://i.imgur.com/cunhmxl.png)

```kotlin
val assetsDir = File("linea-icons")
val srcDir = File("src/main/kotlin")

Svg2Compose.parse(
    applicationIconPackage = "assets",
    accessorName = "LineaIcons",
    outputSourceDirectory = srcDir,
    vectorsDirectory = assetsDir,
    type = VectorType.SVG,
    iconNameTransformer = { name, group -> name.removePrefix(group) },
    allAssetsPropertyName = "AllIcons"
)
```

**Using in code**: `LineaIcons.Arrows.Buhttps://github.com/overpas/svg-to-compose-intellijttonUp`

The project also generate an accessor for all yours assets, for the given example, it should be `LineaIcons.AllIcons` and it also generated for child groups `LineaIcons.Arrows.AllIcons`

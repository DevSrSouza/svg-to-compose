# SVG to Compose

Converts SVG or Android Vector Drawable to Compose code.

### Why this project

Currently [Compose for Desktop](https://www.jetbrains.com/lp/compose/) does not support SVG files and Android Vector Drawables. This also difficulties a migration of the App to Multiplatform (Desktop and Android).

### How its work

The project uses Android's [Svg2Vector](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java) to convert SVG to Android Drawables and uses a customized material icon code generator from the Jetpack Compose [source code](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:compose/material/material/icons/generator/) to generate the source code of the SVG file.

### Example 1: Running with Kotlin Scripting

file: `jetnews-drawables-to-compose.main.kts`

```kotlin
@file:Repository("https://jitpack.io")
@file:Repository("https://maven.google.com")

@file:DependsOn("com.github.DevSrSouza:svg-to-compose:-SNAPSHOT")
@file:DependsOn("com.google.guava:guava:23.0")
@file:DependsOn("com.android.tools:sdk-common:26.3.1")
@file:DependsOn("com.android.tools:common:26.3.1")
@file:DependsOn("com.squareup:kotlinpoet:1.7.2")
@file:DependsOn("org.ogce:xpp3:1.1.6")

import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import java.io.File

val assetsDir = File("assets")
val srcDir = File("src/main/kotlin")

Svg2Compose.parse(
    applicationIconPackage = "assets",
    accessorName = "jetnews_assets",
    outputSourceDirectory = srcDir,
    vectorsDirectory = assetsDir,
    type = VectorType.DRAWABLE
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
    accessorName = "linea_icons",
    outputSourceDirectory = srcDir,
    vectorsDirectory = assetsDir,
    type = VectorType.SVG,
    // Linea icons uses the group in the file name like `Weather/weather_icon.svg`, so we can get the
    // last group(dir) from the icon and remove it from the name
    iconNameTransformer = { name, group -> name.removePrefix(group.split("/").last()) }
)
```

**Using in code**: `LineaIcons.Arrows.ButtonUp`
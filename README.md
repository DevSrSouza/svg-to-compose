# SVG to Compose

Converts SVG or Android Vector Drawable to Compose code.

### Why this project

Currently [Compose for Desktop](https://www.jetbrains.com/lp/compose/) does not support SVG files and Android Vector Drawables. This also difficulties a migration of the App to Multiplatform (Desktop and Android).

### How its work

The project uses Android's [Svg2Vector](https://android.googlesource.com/platform/tools/base/+/master/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java) to convert SVG to Android Drawables and uses a customized material icon code generator from the Jetpack Compose [source code](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:compose/material/material/icons/generator/) to generate the source code of the SVG file.


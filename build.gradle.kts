plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.gmazzo.buildconfig") version "5.4.0"
    id("maven-publish")
}

group = "br.com.devsrsouza"
version = "0.7.0"
description = "Converts SVG or Android Vector Drawable to Compose code."
val baseName = "svg2compose"

val r8: Configuration by configurations.creating

dependencies {
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.android.tools:sdk-common:31.5.1")
    implementation("com.android.tools:common:31.5.1")
    implementation("com.squareup:kotlinpoet:1.18.1")
    implementation("org.ogce:xpp3:1.1.6")
    implementation("com.github.ajalt.clikt:clikt:4.4.0")

    testImplementation(kotlin("test-junit"))

    r8("com.android.tools:r8:8.2.47")
}

tasks.test {
    useJUnit()
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(11)
}

buildConfig {
    buildConfigField("VERSION_NAME", version.toString())
    buildConfigField("DESCRIPTION", description)
    packageName = "br.com.devsrsouza.svg2compose"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}

tasks.withType<Jar>().configureEach {
    archiveBaseName = baseName
    archiveVersion = version.toString()

    manifest {
        attributes["Main-Class"] = "br.com.devsrsouza.svg2compose.MainKt"
        attributes["Implementation-Version"] = version.toString()
    }
}

val fatJar by tasks.registering(Jar::class) {
    dependsOn(configurations.runtimeClasspath)
    dependsOn(tasks.jar)

    from(sourceSets.main.map { it.output.classesDirs + it.output.resourcesDir })
    from(configurations.runtimeClasspath.map { it.asFileTree.files.map(::zipTree) })

    archiveClassifier = "fat"

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude(
        "**/*.kotlin_metadata",
        "**/*.kotlin_builtins",
        "**/*.kotlin_module",
        "**/module-info.class",
        "assets/**",
        "font_metrics.properties",
        "META-INF/AL2.0",
        "META-INF/DEPENDENCIES",
        "META-INF/jdom-info.xml",
        "META-INF/LGPL2.1",
        "META-INF/maven/**",
        "META-INF/native-image/**",
        "META-INF/proguard/**",
        "META-INF/*.version",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "**/*.proto",
        "**/*.dex",
        "**/LICENSE**",
        "**/NOTICE**",
        "r8-version.properties",
        "migrateToAndroidx/*",
        "xsd/catalog.xml",
    )
}

val r8File = layout.buildDirectory.file("libs/$baseName-$version-r8.jar").map { it.asFile }
val rulesFile = project.file("src/main/proguard-rules.pro")
val r8Jar by tasks.registering(JavaExec::class) {
    dependsOn(fatJar)

    val fatJarFile = fatJar.get().archiveFile
    inputs.file(fatJarFile)
    inputs.file(rulesFile)
    outputs.file(r8File)

    classpath(r8)
    mainClass = "com.android.tools.r8.R8"
    args(
        "--release",
        "--classfile",
        "--output", r8File.get().path,
        "--pg-conf", rulesFile.path,
        "--lib", System.getProperty("java.home"),
        fatJarFile.get().toString(),
    )
}

val binaryFile = layout.buildDirectory.file("libs/$baseName-$version-binary.jar").map { it.asFile }
val binaryJar by tasks.registering(Task::class) {
    dependsOn(r8Jar)

    val r8FileProvider = layout.file(r8File)
    val binaryFileProvider = layout.file(binaryFile)
    inputs.files(r8FileProvider)
    outputs.file(binaryFileProvider)

    doLast {
        val r8File = r8FileProvider.get().asFile
        val binaryFile = binaryFileProvider.get().asFile

        binaryFile.parentFile.mkdirs()
        binaryFile.delete()
        binaryFile.writeText("#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n")
        binaryFile.appendBytes(r8File.readBytes())

        binaryFile.setExecutable(true, false)
    }
}

tasks.assemble {
    dependsOn(binaryJar)
}

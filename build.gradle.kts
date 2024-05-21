plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "br.com.devsrsouza"
version = "0.7.0"

dependencies {
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.android.tools:sdk-common:31.5.1")
    implementation("com.android.tools:common:31.5.1")
    implementation("com.squareup:kotlinpoet:1.18.0")
    implementation("org.ogce:xpp3:1.1.6")

    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(11)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}

plugins {
    kotlin("jvm") version "1.9.23"
    id("maven-publish")
}

group = "br.com.devsrsouza"
version = "0.7.0"

dependencies {
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("com.android.tools:sdk-common:31.3.2")
    implementation("com.android.tools:common:31.3.2")
    implementation("com.squareup:kotlinpoet:1.16.0")
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

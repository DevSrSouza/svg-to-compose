import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    maven
}

group = "me.devsrsouza"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://maven.google.com")
}

dependencies {
    implementation("com.google.guava:guava:23.0")
    implementation("com.android.tools:sdk-common:26.3.1")
    implementation("com.android.tools:common:26.3.1")
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("org.ogce:xpp3:1.1.6")

    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
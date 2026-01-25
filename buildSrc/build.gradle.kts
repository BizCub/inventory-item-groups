import java.util.Properties
import kotlin.apply

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.0"
}

val props = Properties().apply {
    project.rootDir.parentFile.resolve("gradle.properties").inputStream().use { load(it) }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}

dependencies {
    implementation("dev.kikugie:stonecutter:${props["plg.stonecutter"]}")
}

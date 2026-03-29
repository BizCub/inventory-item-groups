plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots")
}

dependencies {
    implementation(libs.stonecutter)
    implementation(libs.multiloader)
    implementation(libs.publish)
}

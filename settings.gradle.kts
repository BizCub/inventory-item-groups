pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9+"
}

rootProject.name = extra["mod.name"] as String

stonecutter.create(rootProject) {
    val fb = "fabric"; val fr = "forge"; val nf = "neoforge"
    fun match(version: String, vararg loaders: String) = loaders.forEach {
        var suffix = ""
        if (it == "fabric" && sc.eval(version, "<26.1")) suffix = ".obf"
        if (it == "forge" && sc.eval(version, "<26.1")) suffix = ".arch"
        version("$version-$it", version).buildscript = "scripts/$it$suffix.gradle.kts"
    }
    match("26.1", fb, fr, nf)
    match("1.21.11", fb, fr, nf)
    match("1.21.10", fb, fr, nf)
    match("1.21.8", fb, fr, nf)
    match("1.21.1", fb, fr, nf)
    match("1.20.1", fb, fr)
}

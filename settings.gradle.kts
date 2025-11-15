pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create(rootProject) {
        fun mc(loader: String, vararg versions: String) {
            for (version in versions) version("$version-$loader", version)
        }
        mc("fabric", "1.21.11")
//        mc("forge", "1.16.5", "1.17.1", "1.20.6")
//        mc("neoforge", "1.21")
    }
}

rootProject.name = extra["mod.name"] as String

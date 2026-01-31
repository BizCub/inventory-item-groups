import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

stonecutter active "1.21.11-fabric"

idea.project.settings {
    runConfigurations {
        register<Gradle>("0 Run Client") { taskNames = listOf("runActiveClient") }
        register<Gradle>("0 Run Server") { taskNames = listOf("runActiveServer") }
        register<Gradle>("1 Build Active") { taskNames = listOf("buildActive") }
        register<Gradle>("1 Build All") { taskNames = listOf("buildAndCollect") }
        register<Gradle>("2 Publish Mods") { taskNames = listOf("PublishMods") }
        register<Gradle>("2 Publish Modrinth") { taskNames = listOf("PublishModrinth") }
        register<Gradle>("2 Publish CurseForge") { taskNames = listOf("PublishCurseforge") }
        register<Gradle>("2 Publish GitHub") { taskNames = listOf("PublishGithub") }
    }
}

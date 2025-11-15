import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}
stonecutter active "1.21.11-fabric"

idea.project.settings {
    runConfigurations {
        register<org.jetbrains.gradle.ext.Gradle>("0 Run Client") { this.taskNames = listOf("runActive") }
        register<org.jetbrains.gradle.ext.Gradle>("1 Build Active") { this.taskNames = listOf("buildActive") }
        register<org.jetbrains.gradle.ext.Gradle>("1 Build All") { this.taskNames = listOf("buildAndCollect") }
        register<org.jetbrains.gradle.ext.Gradle>("2 Publish Mods") { this.taskNames = listOf("PublishMods") }
        register<org.jetbrains.gradle.ext.Gradle>("2 Publish Modrinth") { this.taskNames = listOf("PublishModrinth") }
        register<org.jetbrains.gradle.ext.Gradle>("2 Publish CurseForge") { this.taskNames = listOf("PublishCurseforge") }
        register<org.jetbrains.gradle.ext.Gradle>("2 Publish GitHub") { this.taskNames = listOf("PublishGithub") }
    }
}

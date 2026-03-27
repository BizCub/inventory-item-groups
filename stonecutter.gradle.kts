import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    alias(libs.plugins.stonecutter)
    alias(libs.plugins.idea.ext)
}

stonecutter active "26.1-forge"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    swaps["mod_id"] = "\"${prop("mod.id")}\";"
}

idea.project.settings.runConfigurations {
    register<Gradle>("0 Run Client") { taskNames = listOf("runActiveClient") }
    register<Gradle>("0 Run Server") { taskNames = listOf("runActiveServer") }
    register<Gradle>("1 Build Active") { taskNames = listOf("buildActive") }
    register<Gradle>("1 Build All") { taskNames = listOf("buildAndCollect") }
    register<Gradle>("2 Publish Mods") { taskNames = listOf("PublishMods") }
    register<Gradle>("2 Publish Modrinth") { taskNames = listOf("PublishModrinth") }
    register<Gradle>("2 Publish CurseForge") { taskNames = listOf("PublishCurseforge") }
    register<Gradle>("2 Publish GitHub") { taskNames = listOf("PublishGithub") }
    register<Gradle>("3 Generation Source") { taskNames = listOf("genSource") }
}

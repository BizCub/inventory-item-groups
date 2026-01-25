import java.util.Properties

plugins {
    id("java")
}

val customPropsFile = project.rootProject.file("vers/${mod.mc}.properties")!!
if (customPropsFile.exists()) {
    val customProps = Properties().apply { customPropsFile.inputStream().use { load(it) } }
    customProps.forEach { (key, value) -> project.extra[key.toString()] = value }
}

project.extra["loom.platform"] = mod.loader

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/*.toml"),
        "id"            to mod.id,
        "mixin"         to mod.mixin,
        "name"          to mod.name,
        "description"   to mod.description,
        "version"       to mod.version,
        "modrinth"      to mod.modrinth,
        "github"        to mod.github,
        "author"        to "Bizarre Cube",
        "license"       to "MIT"
    )
}

java {
    val java = when {
        scb.current.parsed >= "26.1"    -> JavaVersion.VERSION_25
        scb.current.parsed >= "1.20.5"  -> JavaVersion.VERSION_21
        scb.current.parsed >= "1.18"    -> JavaVersion.VERSION_17
        scb.current.parsed >= "1.17"    -> JavaVersion.VERSION_16
        else                            -> JavaVersion.VERSION_1_8
    }
    targetCompatibility = java
    sourceCompatibility = java
}

plugins {
    id("java")
}

if (isForge) {
    setProp("forge", "${mod.mc}-${getProp("forge")}")

    if (!isClothConfigAvailable) {
        setProp("cloth_config", "17.0.144")
    }
}

if (isNeoForge) {
    val neoVersion = mod.mc.substring(2)
    val neoLoader = getProp("neoforge")
    setProp("neoforge", if (neoVersion.contains(".")) "$neoVersion.$neoLoader" else "$neoVersion.0.$neoLoader")
}

project.extra["loom.platform"] = mod.loader

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:latest.release")
    }
}

base.archivesName.set("${mod.mixin}-${mod.loader}")
version = "${mod.version}+${mod.pub_start}"

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
        scp >= "26.1"   -> 25
        scp >= "1.20.5" -> 21
        scp >= "1.18"   -> 17
        scp >= "1.17"   -> 16
        else            -> 8
    }
    targetCompatibility = JavaVersion.toVersion(java)
    sourceCompatibility = JavaVersion.toVersion(java)
}

plugins {
    id("dev.architectury.loom") version "1.+"
    id("architectury-plugin") version "3.+"
    id("me.modmuss50.mod-publish-plugin") version "1.+"
}

val minecraft = stonecutter.current.version
val snapshot = prop("mc.snapshot").toString()
val loader = loom.platform.get().name.lowercase()
val mixinId = mod.id.replace("_", "-")

val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeoForge = loader == "neoforge"

var pubStart = findProperty("publish.start").toString()
if (pubStart == "null") pubStart = minecraft
var pubEnd = findProperty("publish.end").toString()
if (pubEnd == "null") pubEnd = minecraft

base.archivesName.set("$mixinId-$loader")
version = "${mod.version}+$pubStart"

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else prop("loom.platform")
})

stonecutter {
    swaps["mod_id"] = "\"${prop("mod.id")}\""
    constants.match(loader, "fabric", "forge", "neoforge")
    java {
        val java = if (stonecutter.eval(minecraft, ">=1.20.5")) JavaVersion.VERSION_21
        else if (stonecutter.eval(minecraft, ">=1.18")) JavaVersion.VERSION_17
        else if (stonecutter.eval(minecraft, ">=1.17")) JavaVersion.VERSION_16
        else JavaVersion.VERSION_1_8
        targetCompatibility = java
        sourceCompatibility = java
    }
}

loom {
    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    if (isForge) forge.mixinConfigs("${mixinId}.mixins.json")

    runConfigs.all {
        runDir = "../../run"
    }
}

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${if (snapshot == "null") minecraft else snapshot}")
    mappings(loom.officialMojangMappings())
    modApi("me.shedaniel.cloth:cloth-config-$loader:${mod.cloth_config}")

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:latest.release")
        modApi("com.terraformersmc:modmenu:${mod.modmenu}")
    }
    if (isForge)
        "forge"("net.minecraftforge:forge:$minecraft-${mod.dep("forge_loader")}")
    if (isNeoForge) {
        val neoVers = minecraft.substring(2)
        val neoLoader = mod.dep("neoforge_loader")
        "neoForge"("net.neoforged:neoforge:${if (neoVers.contains(".")) "$neoVers.$neoLoader" else "$neoVers.0.$neoLoader"}")
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "mixin" to mixinId,
        "id" to mod.id,
        "name" to mod.name,
        "description" to mod.description,
        "version" to mod.version,
        "modrinth" to mod.modrinth,
        "github" to mod.github,
        "author" to "Bizarre Cube",
        "license" to "MIT"
    )
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    displayName = "${mod.name} ${loader.upperCaseFirst()} $pubStart v${mod.version}"
    changelog = rootProject.file("CHANGELOG.md").readText()
    version = mod.version
    type = STABLE
    modLoaders.add(loader)
    if (isFabric) modLoaders.add("quilt")

    modrinth {
        projectId = mod.modrinth
        accessToken = file("C:\\Tokens\\modrinth.txt").readText()
        minecraftVersionRange {
            start = pubStart
            end = pubEnd
            includeSnapshots = true
        }
    }

    curseforge {
        projectId = mod.curseforge
        accessToken = file("C:\\Tokens\\curseforge.txt").readText()
        minecraftVersionRange {
            start = pubStart
            end = pubEnd
        }
    }

    github {
        accessToken = file("C:\\Tokens\\github.txt").readText()
        repository = "BizCub/${mod.github}"
        commitish = "master"
        tagName = "v${mod.version}-$loader+$pubStart"
    }
}

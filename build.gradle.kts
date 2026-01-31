plugins {
    `multiloader-loader`
    id("dev.architectury.loom") version "1.+"
    id("me.modmuss50.mod-publish-plugin") version "1.+"
}

stonecutter {
    constants.match(mod.loader, "fabric", "forge", "neoforge")

    swaps["mod_id"] = "\"${prop("mod.id")}\";"

    replacements.string(scp >= "1.21.6") {
        replace("net.minecraft.client.renderer.RenderType", "net.minecraft.client.renderer.RenderPipelines")
    }
    replacements.string(scp >= "1.21.11") {
        replace("import net.minecraft.resources.ResourceLocation", "import net.minecraft.resources.Identifier")
    }
}

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.propIfExist("mc.snapshot", mod.mc)}")
    mappings(loom.officialMojangMappings())

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:latest.release")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.fabric_api}+${mod.mc}")
    }
    if (isForge) {
        "forge"("net.minecraftforge:forge:${mod.mc}-${dep("forge_loader")}")
    }
    if (isNeoForge) {
        "neoForge"("net.neoforged:neoforge:${dep("neoforge_loader")}")
    }
}

publishMods {
    fun tokenDir(token: String) = file("C:\\Tokens\\$token.txt").readText()
    file.set(tasks.remapJar.get().archiveFile)
    displayName = "${mod.name} ${mod.loader.upperCaseFirst()} ${mod.pub_start} v${mod.version}"
    changelog = rootProject.file("CHANGELOG.md").readText()
    version = mod.version
    type = STABLE
    modLoaders.add(mod.loader)
    if (isFabric) modLoaders.add("quilt")

    modrinth {
        projectId = mod.modrinth
        accessToken = tokenDir("modrinth")
        requires("fabric-api")
        minecraftVersionRange {
            start = mod.pub_start
            end = mod.pub_end
            includeSnapshots = true
        }
    }
    curseforge {
        projectId = mod.curseforge
        accessToken = tokenDir("curseforge")
        requires("fabric-api")
        minecraftVersionRange {
            start = mod.pub_start
            end = mod.pub_end
        }
    }
    github {
        accessToken = tokenDir("github")
        repository = "BizCub/${mod.github}"
        commitish = "master"
        tagName = "v${mod.version}-${mod.loader}+${mod.pub_start}"
    }
}

loom {
    if (isForge) forge.mixinConfigs("${mod.mixin}.mixins.json")

    runConfigs.getByName("client") { runDir = "../../run/client" }
    runConfigs.getByName("server") { runDir = "../../run/server" }

    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (scc.isActive) {
    rootProject.tasks.register("buildActive") { dependsOn(tasks.named("buildAndCollect")) }
    rootProject.tasks.register("runActiveClient") { dependsOn(tasks.named("runClient")) }
    rootProject.tasks.register("runActiveServer") { dependsOn(tasks.named("runServer")) }
}

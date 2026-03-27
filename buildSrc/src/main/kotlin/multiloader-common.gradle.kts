plugins {
    id("java")
    id("me.modmuss50.mod-publish-plugin")
}

if (isForge) {
    setProp("forge", "${mod.mc}-${getProp("forge")}")
}

if (isNeoForge) {
    val startIndex = if (!isObfuscated) 0 else 2
    val zero = if (mod.mc.substring(startIndex).contains(".") && isObfuscated) "" else "0."
    setProp("neoforge", "${mod.mc.substring(startIndex)}.$zero${getProp("neoforge")}")
}

project.extra["loom.platform"] = mod.loader

configurations.all {
    resolutionStrategy.force("net.fabricmc:fabric-loader:latest.release")
}

base.archivesName.set("${mod.mixin}-${mod.loader}")
version = "${mod.version}+${mod.pub_start}"

createRunConfiguration()

publishMods {
    fun tokenDir(token: String) = file("C:\\Tokens\\$token.txt").readText()
    displayName = "${mod.name} ${mod.loader.upperCaseFirst()} ${mod.pub_start} v${mod.version}"
    changelog = rootProject.file("CHANGELOG.md").readText()
    version = mod.version
    type = STABLE
    modLoaders.add(mod.loader)
    if (isFabric) modLoaders.add("quilt")

    modrinth {
        projectId = mod.modrinth
        accessToken = tokenDir("modrinth")
        minecraftVersionRange {
            start = mod.pub_start
            end = mod.pub_end
            includeSnapshots = true
        }
    }
    curseforge {
        projectId = mod.curseforge
        accessToken = tokenDir("curseforge")
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

publishPlatforms.forEach { publish ->
    tasks.register<Copy>("publish$publish${mod.mc}") {
        group = "publishing"
        dependsOn("publish$publish")
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (scc.isActive) {
    rootProject.tasks.register("buildActive") { dependsOn(tasks.named("buildAndCollect")) }
    rootProject.tasks.register("runActiveClient") { dependsOn(tasks.named("runClient")) }
    rootProject.tasks.register("runActiveServer") { dependsOn(tasks.named("runServer")) }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["MixinConfigs"] = "${mod.mixin}.mixins.json"
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/*.toml"),
        "ModMenu"       to $$"$ModMenu",
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
    val javaVersion = JavaVersion.toVersion(java)
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

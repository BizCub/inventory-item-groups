plugins {
    multiloader
    alias(libs.plugins.loom)
    alias(libs.plugins.publish)
}

repositories {
    for (rep in reps) maven(rep.name)
}

dependencies {
    minecraft(minecraftDep)

    for (dep in deps) dep.impl(dep.name)
}

loom {
    runConfigs.getByName("client") { runDir = clientRunPath }
    runConfigs.getByName("server") { runDir = serverRunPath }
}

val builtFile = tasks.jar.get().archiveFile

publishMods {
    file.set(builtFile)
}

tasks.named<Copy>("buildAndCollect") {
    from(builtFile)
}

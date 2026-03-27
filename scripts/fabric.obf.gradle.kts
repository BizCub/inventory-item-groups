plugins {
    multiloader
    alias(libs.plugins.loom.remap)
    alias(libs.plugins.publish)
}

repositories {
    for (rep in reps) maven(rep.name)
}

dependencies {
    minecraft(minecraftDep)
    mappings(loom.officialMojangMappings())
    for (dep in deps) dep.modImpl(dep.name)
}

loom {
    runConfigs.getByName("client") { runDir = clientRunPath }
    runConfigs.getByName("server") { runDir = serverRunPath }
}

val builtFile = tasks.remapJar.get().archiveFile

publishMods {
    file.set(builtFile)
}

tasks.named<Copy>("buildAndCollect") {
    from(builtFile)
}

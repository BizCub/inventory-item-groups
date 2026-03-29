plugins {
    multiloader
    alias(libs.plugins.loom)
}

apply(from = ml.scriptPath)

multiloader {
    repositories {
        for (rep in reps) maven(rep.name)
    }

    dependencies {
        minecraft("com.mojang:minecraft:${mod.mcSpecified}")
        if (isObfuscated) "mappings"(loom.officialMojangMappings())
        for (dep in deps) add(if (isObfuscated) dep.modImpl else dep.impl, dep.name)
    }

    loom {
        runConfigs.getByName("client") { runDir = clientRunPath }
        runConfigs.getByName("server") { runDir = serverRunPath }
    }

    val builtFile = if (isObfuscated)
        tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").get().archiveFile
    else
        tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }
}

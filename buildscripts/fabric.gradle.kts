plugins {
    multiloader
    alias(libs.plugins.loom)
}

multiloader {
    setBuiltFile(tasks.named<AbstractArchiveTask>(if (!isObfuscated) "jar" else "remapJar").get().archiveFile)

    repositories {
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        minecraft("com.mojang:minecraft:${mod.mcExact}")
        if (isObfuscated) "mappings"(loom.officialMojangMappings())
        for (dep in deps) add(if (!isObfuscated) dep.configuration else dep.modConfiguration, dep.dependency) {
            for (module in eModules) exclude(module.module)
        }
    }

    loom {
        if (ctFabricFile.exists())
            accessWidenerPath.set(sc.process(ctFabricFile, ctFabricProcessPath))

        runConfigs {
            getByName("client") {
                runDirectory.set(clientRunFile)
            }
            getByName("server") {
                runDirectory.set(serverRunFile)
            }
        }
    }
}

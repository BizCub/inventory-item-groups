plugins {
    multiloader
    alias(libs.plugins.loom)
}

multiloader {
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
        runConfigs.getByName("client") { runDirectory.set(clientRunFile) }
        runConfigs.getByName("server") { runDirectory.set(serverRunFile) }

        val awFile = rootProject.file(ctFabricPath)
        if (awFile.exists()) accessWidenerPath.set(sc.process(awFile, ctFabricProcessPath))
    }

    val builtFile = if (isObfuscated)
        tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").get().archiveFile
    else
        tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks {
        named<Copy>("buildAndCollect") {
            from(builtFile)
        }

        named("validateAccessWidener") {
            dependsOn("processResources")
        }
    }
}

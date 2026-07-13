plugins {
    multiloader
    alias(libs.plugins.loom.arch)
}

multiloader {
    setBuiltFile(tasks.remapJar.get().archiveFile)

    dependencies {
        minecraft("com.mojang:minecraft:${mod.mc}")
        mappings(loom.officialMojangMappings())
        "forge"("net.minecraftforge:forge:${getDep("forge")}")
    }

    loom {
        if (isMainCTFileExist())
            accessWidenerPath.set(ctForgeArchFile)

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

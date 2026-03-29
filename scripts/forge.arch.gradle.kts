plugins {
    multiloader
    alias(libs.plugins.loom.architectury)
}

apply(from = ml.scriptPath)

multiloader {
    loom.silentMojangMappingsLicense()

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(mod.javaNumber))

    repositories {
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        minecraft("com.mojang:minecraft:${mod.mc}")
        mappings(loom.officialMojangMappings())
        "forge"("net.minecraftforge:forge:${getProp("forge")}")
        for (dep in deps) dep.modConfiguration(dep.dependency)
    }

    loom {
        runConfigs.getByName("client") { runDir = clientRunPath }
        runConfigs.getByName("server") { runDir = serverRunPath }

        forge.mixinConfigs("${mod.mixin}.mixins.json")

        decompilers {
            get("vineflower").apply {
                options.put("mark-corresponding-synthetics", "1")
            }
        }
    }

    val builtFile = tasks.remapJar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }
}

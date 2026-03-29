plugins {
    multiloader
    alias(libs.plugins.loom.architectury)
}

apply(from = ml.scriptPath)

multiloader {
    loom.silentMojangMappingsLicense()

    repositories {
        for (rep in reps) maven(rep.name)
    }

    dependencies {
        minecraft("com.mojang:minecraft:${mod.mcSpecified}")
        mappings(loom.officialMojangMappings())
        "forge"("net.minecraftforge:forge:${getProp("forge")}")
        for (dep in deps) dep.impl(dep.name)
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

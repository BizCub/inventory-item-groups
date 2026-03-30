plugins {
    multiloader
    alias(libs.plugins.forge)
}

apply(from = ml.scriptPath)

multiloader {
    repositories {
        minecraft.mavenizer(this)
        maven(fg.forgeMaven)
        maven(fg.minecraftLibsMaven)
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        implementation(minecraft.dependency("net.minecraftforge:forge:${getProp("forge")}"))
        if (scp >= "1.21.6") annotationProcessor("net.minecraftforge:eventbus-validator:7.0.0")
        for (dep in deps) {
            when (dep.id) {
                "cloth-config-forge" -> compileOnly(dep.dependency)
                else -> dep.configuration(dep.dependency)
            }
        }
    }

    minecraft {
        mappings("official", mod.mc)

        runs {
            register("client") {
                workingDir.convention(layout.projectDirectory.dir(clientRunPath))
                args("--mixin.config=${mod.mixin}.mixins.json")
            }
            register("server") {
                workingDir.convention(layout.projectDirectory.dir(serverRunPath))
            }
        }
    }

    val builtFile = tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }
}

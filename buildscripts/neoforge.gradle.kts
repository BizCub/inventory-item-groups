plugins {
    multiloader
    alias(libs.plugins.neoforged)
}

multiloader {
    repositories {
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        for (dep in deps) dep.configuration(dep.dependency) {
            for (module in eModules) exclude(module.module)
        }
    }

    neoForge {
        version = getDep("neoforge")

        runs {
            configureEach {
                disableIdeRun()
            }
            register("client") {
                gameDirectory.set(clientRunFile)
                client()
            }
            register("server") {
                gameDirectory.set(serverRunFile)
                server()
            }
        }

        mods.create(mod.id, Action {
            sourceSet(sourceSets.main.get())
        })

        val acFile = file(atNeoForgePath)
        if (acFile.exists()) accessTransformers.from(acFile)
    }

    val builtFile = tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks {
        named<Copy>("buildAndCollect") {
            from(builtFile)
        }

        named("createMinecraftArtifacts") {
            dependsOn("processResources")
        }
    }
}

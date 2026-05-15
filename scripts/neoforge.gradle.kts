plugins {
    multiloader
    alias(libs.plugins.neoforged)
}

apply(from = ml.scriptPath)

multiloader {
    repositories {
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        for (dep in deps) dep.configuration(dep.dependency)
    }

    neoForge {
        version = getDep("neoforge")

        runs {
            configureEach {
                disableIdeRun()
            }
            register("client") {
                gameDirectory = file(clientRunPath)
                client()
            }
            register("server") {
                gameDirectory = file(serverRunPath)
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

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }

    tasks.named("createMinecraftArtifacts") {
        dependsOn("processResources")
    }
}

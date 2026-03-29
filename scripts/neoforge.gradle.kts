plugins {
    multiloader
    alias(libs.plugins.neoforged)
}

apply(from = ml.scriptPath)

multiloader {
    repositories {
        for (rep in reps) maven(rep.name)
    }

    dependencies {
        for (dep in deps) dep.impl(dep.name)
    }

    neoForge {
        version = getProp("neoforge") as String

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
    }

    val builtFile = tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }
}

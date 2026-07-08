plugins {
    multiloader
    alias(libs.plugins.neoforged)
}

multiloader {
    setBuiltFile(tasks.jar.get().archiveFile)

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

        if (atNeoForgeFile.exists())
            accessTransformers.from(atNeoForgeFile)

        mods.create(mod.id, Action {
            sourceSet(sourceSets.main.get())
        })

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
    }
}

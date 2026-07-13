plugins {
    multiloader
    alias(libs.plugins.neoforged)
}

multiloader {
    setBuiltFile(tasks.jar.get().archiveFile)

    neoForge {
        version = getDep("neoforge")

        if (isMainCTFileExist())
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

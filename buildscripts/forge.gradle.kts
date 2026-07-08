plugins {
    multiloader
    alias(libs.plugins.forge)
}

multiloader {
    setBuiltFile(tasks.jar.get().archiveFile)

    repositories {
        minecraft.mavenizer(this)
        maven(fg.forgeMaven)
        maven(fg.minecraftLibsMaven)
        for (rep in reps) maven(rep.repository)
    }

    dependencies {
        implementation(minecraft.dependency("net.minecraftforge:forge:${getDep("forge")}"))
        if (scp >= "1.21.6") annotationProcessor("net.minecraftforge:eventbus-validator:7.0.0")
        for (dep in deps) dep.configuration(dep.dependency) {
            for (module in eModules) exclude(module.module)
        }
    }

    minecraft {
        mappings("official", mod.mc)
        accessTransformers.from(atForgeFile)

        runs {
            register("client") {
                workingDir.set(clientRunFile)
            }
            register("server") {
                workingDir.set(serverRunFile)
            }
        }
    }
}

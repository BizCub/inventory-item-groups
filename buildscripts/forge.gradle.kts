plugins {
    multiloader
    alias(libs.plugins.forge)
}

multiloader {
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

        runs {
            register("client") {
                workingDir.set(clientRunFile)
                args("--mixin.config=${mod.mixin}.mixins.json")
            }
            register("server") {
                workingDir.set(serverRunFile)
            }
        }

        accessTransformers.from(file(atForgePath))
    }

    val builtFile = tasks.jar.get().archiveFile

    publishMods {
        file.set(builtFile)
    }

    tasks.named<Copy>("buildAndCollect") {
        from(builtFile)
    }
}

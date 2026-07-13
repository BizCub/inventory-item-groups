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
    }

    dependencies {
        implementation(minecraft.dependency("net.minecraftforge:forge:${getDep("forge")}"))
        if (scp >= "1.21.6") annotationProcessor("net.minecraftforge:eventbus-validator:7.0.0")
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

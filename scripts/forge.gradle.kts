plugins {
    multiloader
    alias(libs.plugins.forge)
    alias(libs.plugins.publish)
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)

    for (rep in reps) maven(rep.name)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${getProp("forge")}"))
    annotationProcessor("net.minecraftforge:eventbus-validator:7.0.0")

    for (dep in deps) compileOnly(dep.name)
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

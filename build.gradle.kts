plugins {
    id("io.github.bizcub.multiloader")
}

multiloader {
    sc.replacements {
        string(scp >= "26.1") {
            replace("GuiGraphics", "GuiGraphicsExtractor")
            replace("ClickType", "ContainerInput")
        }
        string(scp >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
    }

    setMREnvironment(mrEnvs.clientOnly)
    setCFEnvironment(cfEnvs.client)

    versionRange("26.2", to = "latest")
    versionRange("1.21.8", to = "1.21.10")
    versionRange("1.21.3", to = "1.21.4")
    versionRange("1.20.6", from = "1.20.6")
    versionRange("1.20.1", to = "1.20.6")
    versionRange("1.20.1", to = "1.20.4", loader = "forge")

    addDependency(
        dependency = "maven.modrinth:simple-config-lib:${getDep("simple-config-lib")}",
        isPublishDepEnabled = true
    )
    val isClothConfigAvailable = !(isForge && scp > "1.21.3")
    addDependency(
        dependency = "me.shedaniel.cloth:cloth-config-${mod.loader}:${getDep("cloth-config").split("+").first()}",
        configuration = if (isClothConfigAvailable) "implementation" else "compileOnly",
        repository = "maven.shedaniel.me",
        isPublishDepEnabled = isClothConfigAvailable,
        publishProjectId = "cloth-config"
    )

    if (isFabric) {
        addDependency(dependency = "net.fabricmc:fabric-loader:${getDep("fabric")}")
        addDependency(
            dependency = "net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}",
            isPublishDepEnabled = true,
            isPublishDepRequired = true
        )
        addDependency(
            dependency = "com.terraformersmc:modmenu:${getDep("modmenu")}",
            repository = "maven.terraformersmc.com/releases",
            isPublishDepEnabled = true
        )
    }
}

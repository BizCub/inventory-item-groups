plugins {
    id("io.github.bizcub.multiloader")
}

multiloader {
    val isClothConfigAvailable = !(isForge && scp > "1.21.3")

    sc.constants["is_cloth_config_available"] = isClothConfigAvailable

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
    versionRange("1.20.1", to = "1.20.6")

    addDependency(dependency = "io.github.bizcub:simple-config-lib:1.0-${mod.loader}+${mod.mc}")

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

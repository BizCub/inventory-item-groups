plugins {
    id("multiloader-common")
    id("me.modmuss50.mod-publish-plugin")
}

sc.constants["is_cloth_config_available"] = isClothConfigAvailable

sc.replacements {
    string(scp >= "26.1") {
        replace("GuiGraphics", "GuiGraphicsExtractor")
        replace("ClickType", "ContainerInput")
        replace("renderTooltip", "extractTooltip")
        replace("renderSlot", "extractSlot")
    }
    string(scp >= "1.21.11") {
        replace("ResourceLocation", "Identifier")
    }
}

if (isForge) {
    if (!isClothConfigAvailable) {
        setProp("cloth_config", "17.0.144")
    }
}

reps.clear()
reps.add(Repository("https://maven.shedaniel.me"))

deps.clear()
deps.add(Dependency("me.shedaniel.cloth:cloth-config-${mod.loader}:${getProp("cloth_config")}", "api"))

if (isFabric) {
    reps.add(Repository("https://maven.terraformersmc.com/releases"))

    deps.add(Dependency("net.fabricmc:fabric-loader:latest.release", "implementation"))
    deps.add(Dependency("net.fabricmc.fabric-api:fabric-api:${getProp("fabric_api")}", "implementation"))
    deps.add(Dependency("com.terraformersmc:modmenu:${getProp("modmenu")}", "api"))
}

if (isNeoForge) {
    reps.add(Repository("https://maven.neoforged.net/releases"))
}

publishMods {
    modrinth {
        if (isClothConfigAvailable) optional("cloth-config")
        if (isFabric) requires("fabric-api")
        if (isFabric) optional("modmenu")
    }
    curseforge {
        if (isClothConfigAvailable) optional("cloth-config")
        if (isFabric) requires("fabric-api")
        if (isFabric) optional("modmenu")
    }
}

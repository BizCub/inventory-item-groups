import com.bizcub.multiloader.MultiLoader
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import me.modmuss50.mpp.ModPublishExtension

val stonecutter = project.extensions.getByType(StonecutterBuildExtension::class.java)

project.extensions.configure<MultiLoader>("multiloader") {
    project.afterEvaluate {
        stonecutter.let { sc ->
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
        }
    }

    addRepository("https://maven.shedaniel.me")
    addDependency("me.shedaniel.cloth:cloth-config-${mod.loader}:${getProp("cloth_config")}", "api")

    if (isFabric) {
        addRepository("https://maven.terraformersmc.com/releases")

        addDependency("net.fabricmc:fabric-loader:latest.release", "implementation")
        addDependency("net.fabricmc.fabric-api:fabric-api:${getProp("fabric_api")}", "implementation")
        addDependency("com.terraformersmc:modmenu:${getProp("modmenu")}", "api")
    }

    if (isNeoForge) {
        addRepository("https://maven.neoforged.net/releases")
    }

    project.extensions.configure<ModPublishExtension>("publishMods") {
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
}

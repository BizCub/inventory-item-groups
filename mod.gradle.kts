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
                }
                string(scp >= "1.21.11") {
                    replace("ResourceLocation", "Identifier")
                }
            }
        }
    }

    addRepository("https://maven.shedaniel.me")
    addDependency("api", "me.shedaniel.cloth:cloth-config-${mod.loader}:${getProp("cloth_config")}")

    if (isFabric) {
        addRepository("https://maven.terraformersmc.com/releases")

        addDependency("implementation", "net.fabricmc:fabric-loader:latest.release")
        addDependency("implementation", "net.fabricmc.fabric-api:fabric-api:${getProp("fabric_api")}")
        addDependency("api", "com.terraformersmc:modmenu:${getProp("modmenu")}")
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

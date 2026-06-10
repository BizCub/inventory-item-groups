import com.bizcub.multiloader.MultiLoader
import dev.kikugie.stonecutter.build.StonecutterBuildExtension

apply(plugin = "dev.kikugie.fletching-table")

val stonecutter = project.extensions.getByType(StonecutterBuildExtension::class.java)

project.extensions.configure<MultiLoader>("multiloader") {
    access()

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

    addDependency("maven.shedaniel.me", "api", "me.shedaniel.cloth:cloth-config-${mod.loader}:${getDep("cloth-config")?.split("+")?.first()}")

    if (isFabric) {
        addDependency("implementation", "net.fabricmc:fabric-loader:${getDep("fabric")}")
        addDependency("implementation", "net.fabricmc.fabric-api:fabric-api:${getDep("fabric-api")}")
        addDependency("maven.terraformersmc.com/releases", "api", "com.terraformersmc:modmenu:${getDep("modmenu")}")
    }

    if (isClothConfigAvailable) addPublishDep("optional", "cloth-config")
    if (isFabric) addPublishDep("requires", "fabric-api")
    if (isFabric) addPublishDep("optional", "modmenu")
}

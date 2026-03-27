import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }
fun String.lowerCaseFirst() = replaceFirstChar { if (it.isUpperCase()) it.lowercaseChar() else it }

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun Project.getProp(key: String) = prop(propName(key))
fun Project.setProp(key: String, value: Any?) = value.also { extra[versionExactlyProp(key)] = it }
fun Project.propName(key: String) = if (prop(versionExactlyProp(key)) != null) versionExactlyProp(key) else versionProp(key)
fun Project.propIf(key: String, fallback: String?) = prop(propName(key)) ?: fallback
fun Project.versionProp(key: String) = "${mod.mc}.$key"
fun Project.versionExactlyProp(key: String) = "${mod.mc}-${mod.loader}.$key"

val Project.sc get() = extensions.getByType<StonecutterBuildExtension>()
val Project.scc get() = sc.current
val Project.scp get() = sc.current.parsed
val Project.scv get() = sc.current.version

val Project.isFabric: Boolean get() = mod.loader == "fabric"
val Project.isForge: Boolean get() = mod.loader == "forge"
val Project.isNeoForge: Boolean get() = mod.loader == "neoforge"
val Project.isObfuscated: Boolean get() = scp < "26.1"
val Project.isClothConfigAvailable: Boolean get() = !(isForge && scp > "1.21.3")

val Project.minecraftDep: String get() = "com.mojang:minecraft:${propIf("version", mod.mc)}"
val Project.clientRunPath: String get() = "../../run/client"
val Project.serverRunPath: String get() = "../../run/server"

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val mc: String get() = project.scv
    val loader: String get() = project.scc.project.substringAfterLast("-")
    val id: String get() = modProp("id")
    val mixin: String get() = modProp("id").replace("_", "-")
    val name: String get() = modProp("name")
    val description: String get() = modProp("description")
    val version: String get() = modProp("version")
    val modrinth: String get() = modProp("modrinth")
    val curseforge: String get() = modProp("curseforge")
    val github: String get() = modProp("github")
    val pub_start: String get() = project.propIf("pub_start", mc).toString()
    val pub_end: String get() = project.propIf("pub_end", mc).toString()

    fun modPropOrNull(key: String) = project.prop("mod.$key")
    fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }
}

var reps = mutableListOf<Repository>()
var deps = mutableListOf<Dependency>()

class Repository(val name: String)
class Dependency(val name: String, val impl: String) {
    val modImpl = "mod${impl.upperCaseFirst()}"
}

val publishPlatforms = listOf("Mods", "Modrinth", "Curseforge", "Github")
fun Project.createRunConfiguration() {
    publishPlatforms.forEach { platform ->
        run {
            val runName = "Publish $platform ${mod.mc}"
            val fileName = runName.replace(" ", "")
            val taskName = fileName.lowerCaseFirst()

            val template = File("buildSrc/src/main/resources/runConfigurationTemplate.xml")
            val destination = File(".idea/runConfigurations/$fileName.xml")

            val newFile = template.copyTo(destination, true)
            val fileText = newFile.readText()
                .replace("%NAME%", runName)
                .replace("%TASK%", taskName)

            newFile.writeText(fileText)
        }
    }
}

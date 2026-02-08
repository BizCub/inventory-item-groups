import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.language.jvm.tasks.ProcessResources

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun Project.dep(key: String): String? = findProperty("dep.$key")?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

val Project.sc get() = extensions.getByType<StonecutterBuildExtension>()
val Project.scc get() = sc.current
val Project.scp get() = sc.current.parsed
val Project.scv get() = sc.current.version

val Project.isFabric: Boolean get() = mod.loader == "fabric"
val Project.isForge: Boolean get() = mod.loader == "forge"
val Project.isNeoForge: Boolean get() = mod.loader == "neoforge"
val Project.isClothConfigAvailable: Boolean get() = !(isForge && scp > "1.21.3")

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
    val pub_start: String get() = propIfExist("pub.start", mc).toString()
    val pub_end: String get() = propIfExist("pub.end", mc).toString()
    val modrinth: String get() = modProp("modrinth")
    val curseforge: String get() = modProp("curseforge")
    val github: String get() = modProp("github")
    val cloth_config: String get() = modProp("cloth_config")
    val modmenu: String get() = modProp("modmenu")

    fun propIfExist(key: String, fallback: String) = if (project.prop(key) != null) project.prop(key) else fallback
    fun modPropOrNull(key: String) = project.prop("mod.$key")
    fun modProp(key: String) = requireNotNull(modPropOrNull(key)) { "Missing 'mod.$key'" }
}

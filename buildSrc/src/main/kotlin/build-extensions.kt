import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id'" }
    val name: String get() = requireNotNull(project.prop("mod.name")) { "Missing 'mod.name'" }
    val description: String get() = requireNotNull(project.prop("mod.description")) { "Missing 'mod.description'" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version'" }
    val modrinth: String get() = requireNotNull(project.prop("mod.modrinth")) { "Missing 'mod.modrinth'" }
    val curseforge: String get() = requireNotNull(project.prop("mod.curseforge")) { "Missing 'mod.curseforge'" }
    val github: String get() = requireNotNull(project.prop("mod.github")) { "Missing 'mod.github'" }
    val mc_end: String get() = requireNotNull(project.prop("mod.mc_end")) { "Missing 'mod.mc_end'" }

    fun dep(key: String) = requireNotNull(project.prop("deps.$key")) { "Missing 'deps.$key'" }
}
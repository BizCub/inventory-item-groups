plugins {
    alias(libs.plugins.stonecutter)
    alias(libs.plugins.multiloader)
}

multiloader.createDepFile()

stonecutter active "26.1.2-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    swaps["mod_id"] = "\"${property("mod.id")}\";"
}

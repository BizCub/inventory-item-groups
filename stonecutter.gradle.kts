plugins {
    alias(libs.plugins.stonecutter)
}

stonecutter active "26.1.1-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    swaps["mod_id"] = "\"${property("mod.id")}\";"
}

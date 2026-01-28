plugins {
    id("multiloader-common")
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:latest.release")
    }
}

if (isForge || isNeoForge) {
    project.extra["mc.snapshot"] = null
}

if (isNeoForge) {
    val neoVers = mod.mc.substring(2)
    val neoLoader = dep("neoforge_loader")
    val neoForge = if (neoVers.contains(".")) "$neoVers.$neoLoader" else "$neoVers.0.$neoLoader"
    project.extra["dep.neoforge_loader"] = neoForge
}

base.archivesName.set("${mod.mixin}-${mod.loader}")
version = "${mod.version}+${mod.pub_start}"

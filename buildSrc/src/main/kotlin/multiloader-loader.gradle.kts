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

    when (mod.mc) {
        "1.21.1" -> project.extra["pub.start"] = "1.21"
    }
}

if (isForge) {
    when (mod.mc) {
        "1.16.5" -> project.extra["pub.end"] = "1.16.5"
        "1.21.1" -> project.extra["pub.start"] = "1.20.6"
    }
}

base.archivesName.set("${mod.mixin}-${mod.loader}")
version = "${mod.version}+${mod.pub_start}"

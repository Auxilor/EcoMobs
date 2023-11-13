pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.jpenilla.xyz/snapshots/")
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "EcoMobs"

// Core
include(":eco-core")
include(":eco-core:core-plugin")
include(":eco-core:core-nms")
include(":eco-core:core-nms:v1_20_R1")

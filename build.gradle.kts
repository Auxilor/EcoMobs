plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.willfp"
version = findProperty("version")!!

base {
    archivesName.set(project.name)
}

dependencies {
    project(":eco-core").dependencyProject.subprojects {
        implementation(this)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/nms/")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://mvn.lumine.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("com.willfp:eco:6.44.0")
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")

        implementation("com.willfp:libreforge:3.127.0")
        implementation("org.joml:joml:1.10.4")
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
            }
        }

        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"

            dependsOn(clean)
        }

        processResources {
            val ignoreList = listOf("**/*.png", "**/models/**", "**/textures/**", "**lang.yml")
            filesNotMatching(ignoreList) {
                expand("projectVersion" to project.version)
            }
        }

        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            relocate("com.willfp.libreforge", "com.willfp.ecobosses.libreforge")
            relocate("org.joml", "com.willfp.ecobosses.libreforge.joml")
        }
    }
}

tasks {
    shadowJar {
        destinationDirectory.set(file("$rootDir/bin"))
    }

    val buyThePlugins by creating {
        dependsOn(subprojects.map { it.tasks.getByName("build") })

        doLast {
            println("If you like the plugin, please consider buying it on Spigot or Polymart!")
            println("Spigot: https://www.spigotmc.org/resources/authors/auxilor.507394/")
            println("Polymart: https://polymart.org/user/auxilor.1107/")
            println("Buying gives you access to support and the plugin auto-updater, and it allows me to keep developing plugins.")
        }
    }

    build {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
        finalizedBy(buyThePlugins)
    }

    clean.get().doLast { file("$rootDir/bin").deleteRecursively() }

    fun fileName(extra: String): String = buildString {
        append(findProperty("plugin-name"))
        append(" v")
        append(findProperty("version"))
        if (extra.isNotEmpty()) {
            append(" ")
            append(extra)
        }
        append(".jar")
    }

    shadowJar.get().archiveFileName.set(fileName(""))
    jar.get().archiveFileName.set(fileName("unshaded"))
}

publishing {
    publications {
        register("maven", MavenPublication::class) {
            from(subprojects.first { it.name == "core-plugin" }.components["java"])
        }
    }
}

group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.github.lokka30:LevelledMobs:3.1.4")
    implementation("com.willfp:ModelEngineBridge:1.2.0")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = project.group.toString()
            version = project.version.toString()
            artifactId = rootProject.name

            artifact(rootProject.tasks.shadowJar.get().archiveFile)
        }
    }

    repositories {
        maven {
            name = "auxilor"
            url = uri("https://repo.auxilor.io/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

tasks {
    build {
        dependsOn(publishToMavenLocal)
    }
}
